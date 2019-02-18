package org.sonar.plugins.clojure.sensors.leinnvd;

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
import org.sonar.plugins.clojure.sensors.AbstractSensor;
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.settings.ClojureProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class LeinNvdSensor extends AbstractSensor implements Sensor {

    private static final Logger LOG = Loggers.get(LeinNvdSensor.class);

    private static final String PLUGIN_NAME = "nvd";
    private static final String[] LEIN_ARGUMENTS = {"nvd", "check"};

    public LeinNvdSensor(CommandRunner commandRunner) {
        super(commandRunner);
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("SonarClojureLeinNvd")
                .onlyOnLanguage(ClojureLanguage.KEY)
                .global();
    }



    @Override
    public void execute(SensorContext context) {

        if (!checkIfPluginIsDisabled(context, ClojureProperties.LEIN_NVD_DISABLED)) {

            if (context.config().get(ClojureProperties.LEIN_NVD_JSON_OUTPUT_LOCATION).isPresent()){
                LOG.info("Running Lein NVD");

                CommandStreamConsumer stdOut =  this.commandRunner.run(LEIN_COMMAND, LEIN_ARGUMENTS);
                if (isLeinInstalled(stdOut.getData()) && isPluginInstalled(stdOut.getData(), PLUGIN_NAME)){
                    FileSystem fs = context.fileSystem();
                    Optional<String> vulnerabilityContext = readFromFileSystem(context.config().get(ClojureProperties.LEIN_NVD_JSON_OUTPUT_LOCATION).get());
                    if (vulnerabilityContext.isPresent()){
                        List<Vulnerability> vulnerabilities = LeinNvdParser.parseJson(vulnerabilityContext.get());
                        saveVulnerabilities(vulnerabilities, context);
                    } else {
                        LOG.warn("Lein NVD dependency report does not exists. Is Lein NVD installed as a plugin?");
                    }
                } else {
                    LOG.warn("Running sensor skipped because Leiningen or Lein NVD are not installed");
                }
            } else {
                LOG.warn("Required property " + ClojureProperties.LEIN_NVD_JSON_OUTPUT_LOCATION + " is not set");
            }

        } else {
            LOG.info("Lein NVD is disabled");
        }

    }

    private void saveVulnerabilities(List<Vulnerability> vulnerabilities, SensorContext context) {
        Optional<InputFile> projectFile = getFile("project.clj", context.fileSystem());

        projectFile.ifPresent(projectFileFromOpt -> {
            for (Vulnerability v :
                    vulnerabilities) {
                LOG.debug("Processing vulnerability: " +v.toString());
                RuleKey ruleKey = RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, "nvd-" + v.getSeverity().toLowerCase());
                NewIssue newIssue = context.newIssue().forRule(ruleKey);
                NewIssueLocation primaryLocation = newIssue
                        .newLocation()
                        .on(projectFileFromOpt)
                        .message(v.getName()
                                + ";" + v.getCwe()
                                + ";" + v.getFileName())
                        .at(projectFileFromOpt.selectLine(1));
                newIssue.at(primaryLocation);
                newIssue.save();
            }
        });

        if (!projectFile.isPresent()){
            LOG.warn("Project.clj is missing - cannot mark vulnerabilities!");
        }
    }

}