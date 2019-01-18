package org.sonar.plugins.clojure.sensors;

import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.sensors.ancient.AncientSensor;

import java.util.List;

public abstract class AbstractSensor {

    private static final Logger LOG = Loggers.get(AncientSensor.class);

    public boolean isLeinInstalled(List<String> output) {
        String leinNotInstalled = "lein: command not found";
        if (!output.toString().contains(leinNotInstalled)) {
            return true;
        } else {
            LOG.error("Leiningen is propably not installed!");
            LOG.error(output.toString());
            return false;
        }
    }

    public boolean isPluginInstalled(List<String> output, String leinTask) {
        String taskNotInstalled = "'" + leinTask + "' is not a task";
        if (!output.toString().contains(taskNotInstalled)) {
            return true;
        } else {
            LOG.error(taskNotInstalled + "  is propably not installed!");
            LOG.error(output.toString());
            return false;
        }
    }

    public boolean checkIfPluginIsDisabled(SensorContext context, String propertyName) {
        LOG.debug("Checking for property: " + propertyName);
        if (context.config().getBoolean(propertyName).isPresent()) {
            LOG.debug(propertyName + " " + context.config().getBoolean(propertyName).get());
            return context.config().getBoolean(propertyName).get();
        } else {
            return false;
        }
    }

}
