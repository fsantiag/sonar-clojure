package org.sonar.plugins.clojure.sensors.leinnvd;

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
import org.sonar.plugins.clojure.settings.NvdProperties;

import java.util.List;
import java.util.Optional;

import static org.sonar.plugins.clojure.settings.NvdProperties.*;
import static org.sonar.plugins.clojure.settings.Properties.SENSORS_TIMEOUT_PROPERTY;
import static org.sonar.plugins.clojure.settings.Properties.SENSORS_TIMEOUT_PROPERTY_DEFAULT;

public class LeinNvdSensor extends AbstractSensor implements Sensor {

    private static final Logger LOG = Loggers.get(LeinNvdSensor.class);

    private static final String[] LEIN_ARGUMENTS = {"nvd", "check"};
    private static final String PLUGIN_NAME = "NVD";

    @SuppressWarnings("WeakerAccess")
    public LeinNvdSensor(CommandRunner commandRunner) {
        super(commandRunner);
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name(PLUGIN_NAME)
                .onlyOnLanguage(ClojureLanguage.KEY)
                .global();
    }

    @Override
    public void execute(SensorContext context) {

        if (!isPluginDisabled(context, PLUGIN_NAME, DISABLED_PROPERTY, DISABLED_PROPERTY_DEFAULT)) {
            LOG.info("Running Lein NVD");
            String reportPath = context.config().get(NvdProperties.REPORT_LOCATION_PROPERTY).orElse(REPORT_LOCATION_DEFAULT);

            long timeOut = context.config().getLong(SENSORS_TIMEOUT_PROPERTY)
                    .orElse(Long.valueOf(SENSORS_TIMEOUT_PROPERTY_DEFAULT));
            this.commandRunner.run(timeOut, LEIN_COMMAND, LEIN_ARGUMENTS);

            Optional<String> vulnerabilityContext = readFromFileSystem(reportPath);
            if (vulnerabilityContext.isPresent()) {
                List<Vulnerability> vulnerabilities = LeinNvdParser.parseJson(vulnerabilityContext.get());
                saveVulnerabilities(vulnerabilities, context);
            } else {
                LOG.warn("Lein NVD dependency report does not exists. Is Lein NVD installed as a plugin?");
            }
        }
    }

    private void saveVulnerabilities(List<Vulnerability> vulnerabilities, SensorContext context) {
        Optional<InputFile> fileOptional = getFile("project.clj", context.fileSystem());

        fileOptional.ifPresent(projectFile -> {
            for (Vulnerability v : vulnerabilities) {
                LOG.debug("Processing vulnerability: " +v.toString());
                RuleKey ruleKey = RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, "nvd-" + v.getSeverity().toLowerCase());
                NewIssue newIssue = context.newIssue().forRule(ruleKey);
                NewIssueLocation primaryLocation = newIssue
                        .newLocation()
                        .on(projectFile)
                        .message(v.getName()
                                + ";" + v.getCwes()
                                + ";" + v.getFileName())
                        .at(projectFile.selectLine(1));
                newIssue.at(primaryLocation);
                newIssue.save();
            }
        });
    }
}