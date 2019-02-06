package org.sonar.plugins.clojure.sensors.cloverage;


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

import java.util.Optional;

public class CloverageSensor extends AbstractSensor implements Sensor {

    private static final Logger LOG = Loggers.get(CloverageSensor.class);

    private static final String PLUGIN_NAME = "cloverage";
    private static final String[] LEIN_ARGUMENTS = {"cloverage", "--codecov"};


    public CloverageSensor(CommandRunner commandRunner) {
        super(commandRunner);
    }

    private void saveCoverageForFile(CoverageReport report, SensorContext context) {

        for (FileAnalysis f : report.getFileEntries()) {
            NewCoverage coverage = context.newCoverage().onFile(f.getFile());
            for (LineAnalysis l : f.getEntries()) {
                LOG.debug("Processing: " + l.getLineNumber() + " " + l.getHits());
                coverage.lineHits(l.getLineNumber(), l.getHits());
            }
            coverage.save();

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

            if (context.config().get(ClojureProperties.CLOVERAGE_JSON_OUTPUT_LOCATION).isPresent()){
                LOG.info("Running Cloverage");
                CommandStreamConsumer stdOut = this.commandRunner.run(LEIN_COMMAND, LEIN_ARGUMENTS);
                if (isLeinInstalled(stdOut.getData()) && isPluginInstalled(stdOut.getData(), PLUGIN_NAME)) {


                    Optional<String> fileString = readFromFileSystem(context.config().get(ClojureProperties.CLOVERAGE_JSON_OUTPUT_LOCATION).get() );
                    if (fileString.isPresent()) {
                        try {
                            CoverageReport report = null;
                            report = CloverageMetricParser.parse(context, fileString.get());
                            saveCoverageForFile(report, context);
                        }  catch (Exception e) {
                            LOG.warn("Running parser or saving caused exception", e);
                        }
                    } else {
                        LOG.warn("Cloverage report does not exists. Have you added added target/coverage/codecov.json to " + ClojureProperties.CLOVERAGE_JSON_OUTPUT_LOCATION + "?");
                    }
                } else {
                    LOG.warn("Parsing skipped because Leiningen or Cloverate are not installed");
                }
            } else {
                LOG.warn("Required property "  + ClojureProperties.CLOVERAGE_JSON_OUTPUT_LOCATION + " is not set. Skipping Cloverage generation");
            }

        }
    }
}