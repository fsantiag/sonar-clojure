package org.sonar.plugins.clojure.sensors.eastwood;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.plugins.clojure.language.Clojure;
import org.sonar.plugins.clojure.sensors.AbstractSensor;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.sensors.Issue;
import org.sonar.plugins.clojure.sensors.LeiningenRunner;
import static org.sonar.plugins.clojure.sensors.eastwood.EastwoodProperties.EASTWOOD_OPTIONS;
import static org.sonar.plugins.clojure.sensors.eastwood.EastwoodProperties.ENABLED_PROPERTY;
import static org.sonar.plugins.clojure.sensors.eastwood.EastwoodProperties.ENABLED_PROPERTY_DEFAULT;
import static org.sonar.plugins.clojure.settings.Properties.SENSORS_TIMEOUT_PROPERTY;
import static org.sonar.plugins.clojure.settings.Properties.SENSORS_TIMEOUT_PROPERTY_DEFAULT;

public class EastwoodSensor extends AbstractSensor implements Sensor {

    private static final Logger LOG = LoggerFactory.getLogger(EastwoodSensor.class);

    private static final String EASTWOOD_COMMAND = "eastwood";
    private static final String SENSOR_NAME = "Eastwood";

    @SuppressWarnings("WeakerAccess")
    public EastwoodSensor(LeiningenRunner leiningenRunner) {
        super(leiningenRunner);
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name(SENSOR_NAME)
                .onlyOnLanguage(Clojure.KEY);
    }

    @Override
    public void execute(SensorContext context) {
        if (isSensorEnabled(context)) {
            LOG.info("Running Eastwood");
            CommandStreamConsumer stdOut = this.leiningenRunner.run(
                    getSensorTimeout(context), EASTWOOD_COMMAND, getSensorOptions(context));

            List<Issue> issues = EastwoodIssueParser.parse(stdOut);

            LOG.debug("Saving issues: " + issues.size());
            issues.forEach(issue -> saveIssue(issue, context));
        } else {
            LOG.info("Eastwood disabled");
        }
    }
    private boolean isSensorEnabled(SensorContext context) {
        Boolean enabled = context.config().getBoolean(ENABLED_PROPERTY).orElse(ENABLED_PROPERTY_DEFAULT);
        LOG.debug(String.format("Property: %s Value: %s", ENABLED_PROPERTY, enabled));
        return enabled;
    }
    private String getSensorOptions(SensorContext context) {
        return context.config().get(EASTWOOD_OPTIONS).orElse(null);
    }
    private long getSensorTimeout(SensorContext context) {
        return context.config().getLong(SENSORS_TIMEOUT_PROPERTY)
                .orElse(Long.valueOf(SENSORS_TIMEOUT_PROPERTY_DEFAULT));
    }
}