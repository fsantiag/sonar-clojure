package org.sonar.plugins.clojure.sensors.eastwood;


import org.sonar.api.Properties;
import org.sonar.api.Property;
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
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.sensors.Issue;

import java.util.List;

public class EastwoodSensor implements Sensor {

    private static final Logger LOG = Loggers.get(EastwoodSensor.class);

    private static final String EASTWOOD_COMMAND = "eastwood";
    private static final String LEIN_COMMAND = "lein";

    private CommandRunner commandRunner;

    public EastwoodSensor(CommandRunner commandRunner) {
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
        descriptor.name("SonarClojure")
                .onlyOnLanguage(ClojureLanguage.KEY)
                .global();
    }

    public boolean checkIfPluginIsDisabled(SensorContext context){

        if (context.config().getBoolean("sonar.clojure.eastwood.disabled").isPresent()) {
            return  context.config().getBoolean("sonar.clojure.eastwood.disabled").get();
        } else {
            return false;
        }
    }

    @Override
    public void execute(SensorContext context) {

        if (!checkIfPluginIsDisabled(context)) {
            LOG.info("Clojure project detected");

            LOG.info("Running Eastwood");
            CommandStreamConsumer stdOut = this.commandRunner.run(LEIN_COMMAND, EASTWOOD_COMMAND);

            String info = EastwoodIssueParser.parseRuntimeInfo(stdOut);
            if (info != null) {
                LOG.info("Ran " + info);
            } else {
                LOG.warn("Eastwood resulted in empty output");
            }

            List<Issue> issues = EastwoodIssueParser.parse(stdOut);
            LOG.info("Saving issues");
            for (Issue issue : issues) {
                saveIssue(issue, context);
            }
        } else {
            LOG.info ("Eastwood plugin is disabled");
        }

    }

}