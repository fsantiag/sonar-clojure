package org.sonar.plugins.clojure.sensors;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import java.util.List;
import java.util.Optional;

public abstract class AbstractSensor {

    private static final Logger LOG = Loggers.get(AbstractSensor.class);

    protected static final String LEIN_COMMAND = "lein";

    protected CommandRunner commandRunner;

    public AbstractSensor(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    public boolean isLeinInstalled(List<String> output) {
        String leinNotInstalled = "lein: command not found";
        if (!output.toString().contains(leinNotInstalled)) {
            return true;
        }
        LOG.error("Leiningen is propably not installed!");
        LOG.error(output.toString());
        return false;

    }

    public boolean isPluginInstalled(List<String> output, String leinTask) {
        String taskNotInstalled = "'" + leinTask + "' is not a task";
        if (!output.toString().contains(taskNotInstalled)) {
            return true;
        }

        LOG.error(taskNotInstalled + "  is propably not installed!");
        LOG.error(output.toString());
        return false;

    }

    public boolean checkIfPluginIsDisabled(SensorContext context, String propertyName) {
        LOG.debug("Checking for property: " + propertyName);

        AtomicBoolean isPropertyEnabled = new AtomicBoolean(false);
        context.config().getBoolean(propertyName).ifPresent(present -> {
            LOG.debug(propertyName + " " + present);
            isPropertyEnabled.set(present);
        });

        return isPropertyEnabled.get();
    }

    protected Optional<InputFile> getFile(String filePath, FileSystem fileSystem) {
        return Optional.ofNullable(fileSystem.inputFile(
                fileSystem.predicates().and(
                        fileSystem.predicates().hasRelativePath(filePath),
                        fileSystem.predicates().hasType(InputFile.Type.MAIN))));
    }

}
