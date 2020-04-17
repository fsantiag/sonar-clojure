package org.sonar.plugins.clojure.sensors.cloverage;


import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.coverage.NewCoverage;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.language.Clojure;
import org.sonar.plugins.clojure.sensors.AbstractSensor;
import org.sonar.plugins.clojure.sensors.CommandRunner;

import java.util.Optional;

import static org.sonar.plugins.clojure.sensors.cloverage.CloverageMetricParser.parse;
import static org.sonar.plugins.clojure.settings.CloverageProperties.*;
import static org.sonar.plugins.clojure.settings.Properties.SENSORS_TIMEOUT_PROPERTY;
import static org.sonar.plugins.clojure.settings.Properties.SENSORS_TIMEOUT_PROPERTY_DEFAULT;

public class CloverageSensor extends AbstractSensor implements Sensor {

    private static final Logger LOG = Loggers.get(CloverageSensor.class);
    private static final String PLUGIN_NAME = "Cloverage";
    private static final String[] LEIN_ARGUMENTS = {"cloverage", "--codecov"};

    @SuppressWarnings("WeakerAccess")
    public CloverageSensor(CommandRunner commandRunner) {
        super(commandRunner);
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name(PLUGIN_NAME)
                .onlyOnLanguage(Clojure.KEY);
    }

    @Override
    public void execute(SensorContext context) {
        if (!isPluginDisabled(context, PLUGIN_NAME, DISABLED_PROPERTY, DISABLED_PROPERTY_DEFAULT)) {
            LOG.info("Running " + PLUGIN_NAME);

            long timeOut = context.config().getLong(SENSORS_TIMEOUT_PROPERTY)
                    .orElse(Long.valueOf(SENSORS_TIMEOUT_PROPERTY_DEFAULT));

            this.commandRunner.run(timeOut, LEIN_COMMAND, LEIN_ARGUMENTS);

            String reportPath = context.config().get(REPORT_LOCATION_PROPERTY).orElse(REPORT_LOCATION_DEFAULT);
            LOG.debug("Using report file: " + reportPath);
            Optional<String> fileString = readFromFileSystem(reportPath);

            if (fileString.isPresent()) {
                try {
                    CoverageReport report = parse(context, fileString.get());
                    saveCoverageForFile(report, context);
                } catch (Exception e) { //TODO this exception is too generic
                    LOG.warn("Error while saving coverage", e);
                }
            } else {
                LOG.warn("Cloverage report does not exist in the given path: " + reportPath);
            }
        }
    }

    private void saveCoverageForFile(CoverageReport report, SensorContext context) {
        report.getFileEntries().forEach(fileAnalysis -> {
            NewCoverage coverage = context.newCoverage().onFile(fileAnalysis.getFile());
            fileAnalysis.getEntries().forEach(lineAnalysis ->
                    coverage.lineHits(lineAnalysis.getLineNumber(), lineAnalysis.getHits()));
            coverage.save();
        });
    }
}