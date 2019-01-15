package org.sonar.plugins.clojure.sensors.kibit;


import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.language.ClojureLanguage;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.sensors.Issue;

import java.util.List;

public class KibitSensor implements Sensor {

    private static final Logger LOG = Loggers.get(KibitSensor.class);

    private static final String KIBIT_COMMAND = "kibit";
    private static final String LEIN_COMMAND = "lein";

    private CommandRunner commandRunner;

    public KibitSensor(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    private void saveIssue(Issue issue, SensorContext context) {
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

    private InputFile getFile(Issue issue, FileSystem fileSystem) {
        return fileSystem.inputFile(
                fileSystem.predicates().and(
                        fileSystem.predicates().hasRelativePath(issue.getFilePath()),
                        fileSystem.predicates().hasType(InputFile.Type.MAIN)));
    }


    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("SonarClojureKibit")
                .onlyOnLanguage(ClojureLanguage.KEY)
                .global();
    }

    @Override
    public void execute(SensorContext context) {
        LOG.info("Running Kibit");
        CommandStreamConsumer stdOut = this.commandRunner.run(LEIN_COMMAND, KIBIT_COMMAND, "-r", "markdown");

        List<Issue> issues = KibitIssueParser.parse(stdOut);
        LOG.info("Saving issues");
        for (Issue issue : issues) {
            saveIssue(issue, context);
        }
    }

}