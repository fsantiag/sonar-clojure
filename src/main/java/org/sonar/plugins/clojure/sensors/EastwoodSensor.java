package org.sonar.plugins.clojure.sensors;


import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.language.ClojureLanguage;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EastwoodSensor implements Sensor {

    private static final Logger LOG = Loggers.get(EastwoodSensor.class);

    private static final long EASTWOOD_TIMEOUT = 600_00;
    private static final Pattern EASTWOOD_PATTERN = Pattern.compile("([^:]+):(\\d+):(\\d+):([\\s\\w-]+):(.*)");
    private static final String EASTWOOD_COMMAND = "eastwood";
    private static final String LEIN_COMMAND = "lein";

    private FileSystem fileSystem;

    public EastwoodSensor(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    private void saveIssue(Issue issue, SensorContext context) {
        InputFile file = getFile(issue);

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

    private InputFile getFile(Issue issue) {
        return fileSystem.inputFile(
                fileSystem.predicates().and(
                        fileSystem.predicates().hasRelativePath(issue.getFilePath()),
                        fileSystem.predicates().hasType(InputFile.Type.MAIN)));
    }

    private List<Issue> executeEastwood() {
        CommandStreamConsumer stdOut = new CommandStreamConsumer();
        CommandStreamConsumer stdErr = new CommandStreamConsumer();

        Command command = Command.create(LEIN_COMMAND).addArgument(EASTWOOD_COMMAND);

        CommandExecutor.create().execute(command, stdOut, stdErr, EASTWOOD_TIMEOUT);

        return parseCommandOutputIntoIssues(stdOut);
    }

    private List<Issue> parseCommandOutputIntoIssues(CommandStreamConsumer commandOutput) {
        List<Issue> issues = new ArrayList<>();

        for (String line : commandOutput.getData()) {
            Matcher matcher = EASTWOOD_PATTERN.matcher(line);

            if (matcher.find()) {
                String externalRuleId = matcher.group(4);
                String description = matcher.group(5);
                String filePath = matcher.group(1);
                int lineNumber = Integer.parseInt(matcher.group(2));

                issues.add(new Issue(externalRuleId, description, filePath, lineNumber));
            }
        }

        return issues;
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("SonarClojure")
                .onlyOnLanguage(ClojureLanguage.KEY)
                .global();
    }

    @Override
    public void execute(SensorContext context) {
        LOG.info("Clojure project detected, running SonarClojure");

        LOG.info("Running Eastwood");
        List<Issue> issues = executeEastwood();

        LOG.info("Saving issues");
        for (Issue issue : issues) {
            saveIssue(issue, context);
        }
    }

}