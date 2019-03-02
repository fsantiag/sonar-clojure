package org.sonar.plugins.clojure.sensors;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

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

    private InputFile getFile(Issue issue, FileSystem fileSystem) {
        return fileSystem.inputFile(
                fileSystem.predicates().and(
                        fileSystem.predicates().hasRelativePath(issue.getFilePath()),
                        fileSystem.predicates().hasType(InputFile.Type.MAIN)));
    }

    protected void saveIssue(Issue issue, SensorContext context) {
        InputFile file = getFile(issue, context.fileSystem());

        if (file == null) {
            LOG.warn("Not able to find a file with path '{}'", issue.getFilePath());
            return;
        }

        RuleKey ruleKey = RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, issue.getExternalRuleId().trim());

        NewIssue newIssue = context.newIssue().forRule(ruleKey);

        NewIssueLocation primaryLocation = newIssue
                .newLocation()
                .on(file)
                .message(issue.getDescription().trim());

        primaryLocation.at(file.selectLine(issue.getLine()));

        newIssue.at(primaryLocation);
        newIssue.save();
    }

    /**
     * Gets the file directly from filesystem. This is useful when the file is needed to be read which is not wanted to
     * be part of SonarQube scanning
     * @param filename
     * @return
     */
    public Optional<String> readFromFileSystem(String filename){
        try {
            return Optional.of(new String(Files.readAllBytes(Paths.get(filename)), UTF_8));
        } catch (IOException e) {
            return Optional.empty();
        }

    }


}
