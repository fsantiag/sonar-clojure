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
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CloverageSensor implements Sensor {

    private static final Logger LOG = Loggers.get(CloverageSensor.class);

    private static final String CLOVERAGE_COMMAND = "cloverage";
    private static final String LEIN_COMMAND = "lein";

    private CommandRunner commandRunner;

    public CloverageSensor(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    private void saveCoverageForFile(CoverageReport report, SensorContext context) {

        for (FileAnalysis f: report.getFileEntries()) {
            LOG.debug("Processing file: " + f.getPath());
            InputFile sourceFile = getFile(f.getPath(), context.fileSystem());
            NewCoverage coverage = context.newCoverage().onFile(sourceFile);
            for (LineAnalysis l: f.getEntries()) {
                LOG.debug("Processing: " + l.getLineNumber() + " " + l.getHits());
                coverage.lineHits(l.getLineNumber(), l.getHits());
            }
            coverage.save();
        }

    }

    private InputFile getFile(String filePath, FileSystem fileSystem) {
        return fileSystem.inputFile(
                fileSystem.predicates().and(
                        fileSystem.predicates().hasRelativePath(filePath),
                        fileSystem.predicates().hasType(InputFile.Type.MAIN)));
    }


    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("SonarClojureCloverage")
                .onlyOnLanguage(ClojureLanguage.KEY)
                .global();
    }

    @Override
    public void execute(SensorContext context) {
        LOG.info("Running Cloverage");
        CommandStreamConsumer stdOut = this.commandRunner.run(LEIN_COMMAND, CLOVERAGE_COMMAND, "--codecov");
        InputFile file = getFile("target/coverage/codecov.json", context.fileSystem());
        if (file != null){
            CoverageReport report = null;
            try {
                report = CloverageMetricParser.parse(file.contents());
                saveCoverageForFile(report, context);
            } catch (IOException e) {
                LOG.warn("Cloverage report cannot be read");
            } catch (Exception e){
                LOG.warn("Running parser or saving caused exception");
                e.printStackTrace();
            }
        } else {
            LOG.warn("Cloverage report does not exists. Have you added Cloverage to plugin and also added target/coverage/codecov.json to SonarQube source? ");
        }
    }

}