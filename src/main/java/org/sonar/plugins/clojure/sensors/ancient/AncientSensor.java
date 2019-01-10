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
import org.sonar.plugins.clojure.sensors.leinNvd.LeinNvdParser;
import org.sonar.plugins.clojure.sensors.leinNvd.Vulnerability;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    @Override
    public void execute(SensorContext context) {
        LOG.info("Running Lein Ancient");

        CommandStreamConsumer stdOut = this.commandRunner.run(LEIN_COMMAND, "ancient");
        List<OutdatedDependency> outdated = AncientOutputParser.parse(stdOut.getData());
        LOG.debug("Parsed " + outdated.size() + " dependencies");
        saveOutdated(outdated, context);
    }


    private void saveOutdated(List<OutdatedDependency> outdated, SensorContext context) {

        try {
            for (OutdatedDependency o :
                    outdated) {
                InputFile forSonarQube = getFile("project.clj", context.fileSystem());
                ProjectFile pr = new ProjectFile(forSonarQube.contents());
                LOG.debug("Processing outdated dependencies");

                RuleKey ruleKey = RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, "ancient-dependency");
                NewIssue newIssue = context.newIssue().forRule(ruleKey);
                int lineLocation = pr.findLineNumber(o.getName() +" \"" + o.getCurrentVersion() + "\"");

                NewIssueLocation primaryLocation = newIssue
                        .newLocation()
                        .on(forSonarQube)
                        .message(o.toString())
                        .at(forSonarQube.selectLine(lineLocation));
                newIssue.at(primaryLocation);
                newIssue.save();
            }

        } catch (IOException e) {
            LOG.warn("project.clj could not be read");
        }


    }

}