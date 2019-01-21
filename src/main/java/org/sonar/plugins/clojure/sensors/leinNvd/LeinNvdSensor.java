package org.sonar.plugins.clojure.sensors.leinNvd;


import jdk.internal.util.xml.impl.Input;
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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class LeinNvdSensor extends AbstractSensor implements Sensor {

    private static final Logger LOG = Loggers.get(LeinNvdSensor.class);

    private static final String COMMAND = "nvd";

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
        LOG.info("Running Lein NVD");
        this.commandRunner.run(LEIN_COMMAND, COMMAND, "check");
        FileSystem fs = context.fileSystem();
        InputFile vulnerabilityFile = fs.inputFile(fs.predicates().hasRelativePath("target/nvd/dependency-check-report.json"));

        if (vulnerabilityFile != null){
            List<Vulnerability> vulnerabilities = null;
            try {
                vulnerabilities = LeinNvdParser.parseJson(vulnerabilityFile.contents());
            } catch (IOException e) {
                LOG.warn("Lein NVD dependency report cannot be read");
            }
            saveVulnerabilities(vulnerabilities, context);
        } else {
            LOG.warn("Lein NVD dependency report does not exists. Is lein-nvd installed as a plugin?");
        }
    }

    private void saveVulnerabilities(List<Vulnerability> vulnerabilities, SensorContext context) {
        Optional<InputFile> projectFile = getFile("project.clj", context.fileSystem());

        if (projectFile.isPresent()){
            InputFile projectFileFromOptional = projectFile.get();
            for (Vulnerability v :
                    vulnerabilities) {
                LOG.debug("Processing vulnerability: " +v.toString());
                RuleKey ruleKey = RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, "nvd-" + v.getSeverity().toLowerCase());
                NewIssue newIssue = context.newIssue().forRule(ruleKey);
                NewIssueLocation primaryLocation = newIssue
                        .newLocation()
                        .on(projectFileFromOptional)
                        .message(v.getName()
                                + ";" + v.getCwe()
                                + ";" + v.getFileName())
                        .at(projectFileFromOptional.selectLine(1));
                newIssue.at(primaryLocation);
                newIssue.save();
            }
        } else {
            LOG.warn("Project.clj is missing - cannot mark vulnerabilities!");
        }
    }

}