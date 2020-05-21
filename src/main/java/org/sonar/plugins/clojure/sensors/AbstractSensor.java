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
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class AbstractSensor {

    private static final Logger LOG = Loggers.get(AbstractSensor.class);

    protected static final String LEIN_COMMAND =
            System.getProperty("os.name").toUpperCase().contains("WINDOWS") ? "lein.bat" : "lein";

    protected CommandRunner commandRunner;

    public AbstractSensor(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    protected boolean isPluginDisabled(SensorContext context, String pluginName, String propertyName, boolean defaultValue) {
        Boolean pluginDisabled = context.config().getBoolean(propertyName).orElse(defaultValue);
        LOG.debug(String.format("Property: %s Value: %s", propertyName, pluginDisabled));
        if (pluginDisabled) {
            LOG.info(pluginName + " disabled");
        }
        return pluginDisabled;
    }

    protected Optional<InputFile> getFile(String filePath, FileSystem fileSystem) {
        return Optional.ofNullable(fileSystem.inputFile(
                fileSystem.predicates().and(
                        fileSystem.predicates().hasRelativePath(filePath),
                        fileSystem.predicates().hasType(InputFile.Type.MAIN))));
    }


    protected void saveIssue(Issue issue, SensorContext context) {
        try {
            Optional<InputFile> fileOptional = getFile(issue.getFilePath(), context.fileSystem());

            if (fileOptional.isPresent()) {
                InputFile file = fileOptional.get();
                RuleKey ruleKey = RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, issue.getExternalRuleId().trim());

                NewIssue newIssue = context.newIssue().forRule(ruleKey);

                NewIssueLocation primaryLocation = newIssue
                        .newLocation()
                        .on(file)
                        .message(issue.getDescription().trim());

                primaryLocation.at(file.selectLine(issue.getLine()));

                newIssue.at(primaryLocation);
                newIssue.save();
            } else {
                LOG.warn("Not able to find a file with path '{}'", issue.getFilePath());
            }
        } catch (Exception e) {
            LOG.error("Can not save the issue due to: " + e.getMessage());
        }
    }

    /**
     * Gets the file directly from filesystem. This is useful when the file is needed to be read which is not wanted to
     * be part of SonarQube scanning
     */
    public Optional<String> readFromFileSystem(String filename){
        try {
            return Optional.of(new String(Files.readAllBytes(Paths.get(filename)), UTF_8));
        } catch (IOException e) {
            return Optional.empty();
        }

    }
}
