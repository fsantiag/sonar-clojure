package org.sonar.plugins.clojure.sensors.kibit;


import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.language.Clojure;
import org.sonar.plugins.clojure.sensors.AbstractSensor;
import org.sonar.plugins.clojure.sensors.LeiningenRunner;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.sensors.Issue;
import org.sonar.plugins.clojure.settings.KibitProperties;

import java.util.List;

import static org.sonar.plugins.clojure.settings.Properties.SENSORS_TIMEOUT_PROPERTY;
import static org.sonar.plugins.clojure.settings.Properties.SENSORS_TIMEOUT_PROPERTY_DEFAULT;

public class KibitSensor extends AbstractSensor implements Sensor {

    private static final Logger LOG = Loggers.get(KibitSensor.class);

    private static final String KIBIT_COMMAND = "kibit";
    private static final String PLUGIN_NAME = "Kibit";

    @SuppressWarnings("WeakerAccess")
    public KibitSensor(LeiningenRunner leiningenRunner) {
        super(leiningenRunner);
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name(PLUGIN_NAME)
                .onlyOnLanguage(Clojure.KEY);
    }

    @Override
    public void execute(SensorContext context) {
        if (isPluginEnabled(context, PLUGIN_NAME, KibitProperties.ENABLED_PROPERTY, KibitProperties.ENABLED_PROPERTY_DEFAULT)) {
            LOG.info("Running Kibit");
            long timeOut = context.config().getLong(SENSORS_TIMEOUT_PROPERTY)
                    .orElse(Long.valueOf(SENSORS_TIMEOUT_PROPERTY_DEFAULT));

            CommandStreamConsumer stdOut = this.leiningenRunner.run(timeOut, KIBIT_COMMAND);

            List<Issue> issues = KibitIssueParser.parse(stdOut);
            LOG.info("Saving issues");
            for (Issue issue : issues) {
                saveIssue(issue, context);
            }

        }
    }
}