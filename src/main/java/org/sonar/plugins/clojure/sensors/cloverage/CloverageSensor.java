package org.sonar.plugins.clojure.sensors.cloverage;


import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.coverage.NewCoverage;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.language.ClojureLanguage;
import org.sonar.plugins.clojure.sensors.AbstractSensor;
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.settings.ClojureProperties;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class CloverageSensor extends AbstractSensor implements Sensor {

    private static final Logger LOG = Loggers.get(CloverageSensor.class);

    private static final String CLOVERAGE_COMMAND = "cloverage";


    public CloverageSensor(CommandRunner commandRunner) {
        super(commandRunner);
    }

    private void saveCoverageForFile(CoverageReport report, SensorContext context) {

        for (FileAnalysis f : report.getFileEntries()) {
            LOG.debug("Processing file: " + f.getPath());
            getFile(f.getPath(), context.fileSystem()).ifPresent(sourceFile -> {

                        NewCoverage coverage = context.newCoverage().onFile(sourceFile);
                        for (LineAnalysis l : f.getEntries()) {
                            LOG.debug("Processing: " + l.getLineNumber() + " " + l.getHits());
                            coverage.lineHits(l.getLineNumber(), l.getHits());
                        }
                        coverage.save();
                    }
            );
        }

    }


    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("SonarClojureCloverage")
                .onlyOnLanguage(ClojureLanguage.KEY)
                .global();
    }

    @Override
    public void execute(SensorContext context) {
        if (!checkIfPluginIsDisabled(context, ClojureProperties.CLOVERAGE_DISABLED)) {
            LOG.info("Running Cloverage");
            CommandStreamConsumer stdOut = this.commandRunner.run(LEIN_COMMAND, CLOVERAGE_COMMAND, "--codecov");
            if (isLeinInstalled(stdOut.getData()) && isPluginInstalled(stdOut.getData(), CLOVERAGE_COMMAND)) {
                Optional<InputFile> file = getFile("target/coverage/codecov.json", context.fileSystem());
                if (file.isPresent()) {
                    try {
                        CoverageReport report = null;
                        report = CloverageMetricParser.parse(file.get().contents());
                        saveCoverageForFile(report, context);
                    } catch (IOException e) {
                        LOG.warn("Cloverage report cannot be read");
                    } catch (Exception e) {
                        LOG.warn("Running parser or saving caused exception");
                        e.printStackTrace();
                    }
                } else {
                    LOG.warn("Cloverage report does not exists. Have you added added target/coverage/codecov.json to SonarQube source? ");
                }
            } else {
                LOG.warn("Parsing skipped because Leiningen or Cloverate are not installed");
            }
        }
    }
}