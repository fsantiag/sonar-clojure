package org.sonar.plugins.clojure.sensors.ancient;


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
import org.sonar.plugins.clojure.leiningen.ProjectFile;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;
import org.sonar.plugins.clojure.sensors.AbstractSensor;
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.settings.ClojureProperties;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AncientSensor extends AbstractSensor implements Sensor {

    private static final Logger LOG = Loggers.get(AncientSensor.class);

    private static final String LEIN_ARGUMENTS = "ancient";

    public AncientSensor(CommandRunner commandRunner) {
        super(commandRunner);
    }


    public Optional<InputFile> getFile(String filePath, FileSystem fileSystem) {
        return Optional.ofNullable(fileSystem.inputFile(
                fileSystem.predicates().and(
                        fileSystem.predicates().hasRelativePath(filePath),
                        fileSystem.predicates().hasType(InputFile.Type.MAIN))));
    }


    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("SonarClojureAncient")
                .onlyOnLanguage(ClojureLanguage.KEY)
                .global();
    }

    @Override
    public void execute(SensorContext context) {

        if (!checkIfPluginIsDisabled(context, ClojureProperties.ANCIENT_CLJ_DISABLED)) {
            LOG.info("Running Lein Ancient");
            CommandStreamConsumer stdOut = this.commandRunner.run(LEIN_COMMAND, LEIN_ARGUMENTS);
            if (isLeinInstalled(stdOut.getData()) && isPluginInstalled(stdOut.getData(), LEIN_ARGUMENTS)) {
                List<OutdatedDependency> outdatedDependencies = AncientOutputParser.parse(stdOut.getData());
                LOG.debug("Parsed " + outdatedDependencies.size() + " dependencies");
                saveOutdated(outdatedDependencies, context);
            } else {
                LOG.warn("Parsing skipped because Leiningen or Ancient are not installed");
            }
        } else {
            LOG.info("Ancient is disabled");
        }
    }

    private void saveOutdated(List<OutdatedDependency> outdatedDependencies, SensorContext context) {

        Optional<InputFile> projectFileOptional = getFile("project.clj", context.fileSystem());

        projectFileOptional.ifPresent(inputFile -> outdatedDependencies.stream().forEach(outdatedDependency -> {
            ProjectFile pr = null;
            try {
                pr = new ProjectFile(inputFile.contents());
            } catch (IOException e) {
                LOG.warn("project.clj could not be read");
            }
            LOG.debug("Processing outdated dependencies");

            RuleKey ruleKey = RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, "ancient-clj-dependency");
            NewIssue newIssue = context.newIssue().forRule(ruleKey);
            int lineNumber = pr.findLineNumber(
                    outdatedDependency.getName() + " \"" + outdatedDependency.getCurrentVersion() + "\"");

            NewIssueLocation primaryLocation = newIssue
                    .newLocation()
                    .on(inputFile)
                    .message(outdatedDependency.toString())
                    .at(inputFile.selectLine(lineNumber));
            newIssue.at(primaryLocation);
            newIssue.save();
        }));
    }

}