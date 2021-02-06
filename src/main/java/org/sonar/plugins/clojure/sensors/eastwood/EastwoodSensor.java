package org.sonar.plugins.clojure.sensors.eastwood;


import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.language.Clojure;
import org.sonar.plugins.clojure.sensors.AbstractSensor;
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.sensors.Issue;

import java.util.List;
import java.util.Optional;

import static org.sonar.plugins.clojure.settings.EastwoodProperties.*;
import static org.sonar.plugins.clojure.settings.Properties.SENSORS_TIMEOUT_PROPERTY;
import static org.sonar.plugins.clojure.settings.Properties.SENSORS_TIMEOUT_PROPERTY_DEFAULT;

public class EastwoodSensor extends AbstractSensor implements Sensor {

    private static final Logger LOG = Loggers.get(EastwoodSensor.class);

    private static final String EASTWOOD_COMMAND = "eastwood";
    private static final String PLUGIN_NAME = "Eastwood";

    @SuppressWarnings("WeakerAccess")
    public EastwoodSensor(CommandRunner commandRunner) {
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
            LOG.info("Running Eastwood");
            String options = context.config().get(EASTWOOD_OPTIONS).orElse(null);
            long timeOut = context.config().getLong(SENSORS_TIMEOUT_PROPERTY)
                    .orElse(Long.valueOf(SENSORS_TIMEOUT_PROPERTY_DEFAULT));

            CommandStreamConsumer stdOut = this.commandRunner.run(timeOut, LEIN_COMMAND, EASTWOOD_COMMAND, options);

            List<Issue> issues = EastwoodIssueParser.parse(stdOut);
            LOG.info("Saving issues " + issues.size());
            for (Issue issue : issues) {
                saveIssue(issue, context);
            }
        }
    }
}