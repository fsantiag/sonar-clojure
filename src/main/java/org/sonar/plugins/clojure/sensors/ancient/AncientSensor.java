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
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;

import java.io.IOException;
import java.util.List;

public class AncientSensor implements Sensor {

    private static final Logger LOG = Loggers.get(AncientSensor.class);

    private static final String LEIN_COMMAND = "lein";

    private CommandRunner commandRunner;

    public AncientSensor(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }


    private InputFile getFile(String filePath, FileSystem fileSystem) {
        return fileSystem.inputFile(
                fileSystem.predicates().and(
                        fileSystem.predicates().hasRelativePath(filePath),
                        fileSystem.predicates().hasType(InputFile.Type.MAIN)));
    }


    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("SonarClojureAncient")
                .onlyOnLanguage(ClojureLanguage.KEY)
                .global();
    }

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

    public boolean isAncientInstalled(List<String> output) {
        String ancientNotInstalled = "'ancient' is not a task";
        if (!output.toString().contains(ancientNotInstalled)) {
            return true;
        } else {
            LOG.error("Ancient is propably not installed!");
            LOG.error(output.toString());
            return false;
        }
    }

    @Override
    public void execute(SensorContext context) {
        LOG.info("Running Lein Ancient");

        CommandStreamConsumer stdOut = this.commandRunner.run(LEIN_COMMAND, "ancient");
        if (isLeinInstalled(stdOut.getData()) && isAncientInstalled(stdOut.getData())) {
            List<OutdatedDependency> outdated = AncientOutputParser.parse(stdOut.getData());
            LOG.debug("Parsed " + outdated.size() + " dependencies");
            saveOutdated(outdated, context);
        } else {
            LOG.warn("Parsing skipped because Leiningen or Ancient is not installed");
        }
    }

    public boolean checkIfPluginIsDisabled(SensorContext context) {

        if (context.config().getBoolean("sonar.clojure.eastwood.disabled").isPresent()) {
            return context.config().getBoolean("sonar.clojure.eastwood.disabled").get();
        } else {
            return false;
        }
    }


    private void saveOutdated(List<OutdatedDependency> outdated, SensorContext context) {
        if (!checkIfPluginIsDisabled(context)) {
            InputFile project = getFile("project.clj", context.fileSystem());
            if (project != null) {
                try {
                    for (OutdatedDependency o :
                            outdated) {


                        ProjectFile pr = new ProjectFile(project.contents());
                        LOG.debug("Processing outdated dependencies");

                        RuleKey ruleKey = RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, "ancient-clj-dependency");
                        NewIssue newIssue = context.newIssue().forRule(ruleKey);
                        int lineLocation = pr.findLineNumber(o.getName() + " \"" + o.getCurrentVersion() + "\"");

                        NewIssueLocation primaryLocation = newIssue
                                .newLocation()
                                .on(project)
                                .message(o.toString())
                                .at(project.selectLine(lineLocation));
                        newIssue.at(primaryLocation);
                        newIssue.save();
                    }

                } catch (IOException e) {
                    LOG.warn("project.clj could not be read");
                }
            } else {
                LOG.warn("project.clj does not exists in the filesystem");
            }
        }
    }
}