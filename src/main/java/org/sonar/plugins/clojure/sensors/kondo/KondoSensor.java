package org.sonar.plugins.clojure.sensors.kondo;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.rule.Severity;
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

import java.util.List;
import java.util.Optional;

import static org.sonar.plugins.clojure.settings.KondoProperties.DISABLED_PROPERTY;
import static org.sonar.plugins.clojure.settings.KondoProperties.DISABLED_PROPERTY_DEFAULT;
import static org.sonar.plugins.clojure.settings.Properties.SENSORS_TIMEOUT_PROPERTY;
import static org.sonar.plugins.clojure.settings.Properties.SENSORS_TIMEOUT_PROPERTY_DEFAULT;

public class KondoSensor extends AbstractSensor implements Sensor {

    private static final Logger LOG = Loggers.get(KondoSensor.class);

    private static final String PLUGIN_NAME = "clj-kondo";
    private static final String[] COMMAND =
            {"with-profile", "analysis", "run", "-m", "clj-kondo.main", "--lint", "src", "--config", "{:output {:format :edn}}"};

    public KondoSensor(CommandRunner commandRunner) {
        super(commandRunner);
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name(PLUGIN_NAME)
                .onlyOnLanguage(ClojureLanguage.KEY)
                .global();
    }

    private void saveIssue(Finding finding, SensorContext context) {
        String filename = finding.getFilename();
        try {
            Optional<InputFile> fileOptional = getFile(filename, context.fileSystem());

            if (fileOptional.isPresent()) {
                InputFile file = fileOptional.get();
                RuleKey ruleKey = RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, finding.getType());

                NewIssue newIssue = context.newIssue().forRule(ruleKey);

                NewIssueLocation primaryLocation = newIssue
                        .newLocation()
                        .on(file)
                        .message(finding.getMessage());

                TextRange range = file.newRange(finding.getRow(), finding.getCol(),
                        finding.getEndRow(), finding.getEndCol());
                primaryLocation.at(range);

                newIssue.at(primaryLocation);
                newIssue.overrideSeverity(getSeverity(finding.getLevel()));
                newIssue.save();
            } else {
                LOG.warn("Not able to find a file with path '{}'", filename);
            }
        } catch (Exception e) {
            LOG.error("Can not save the issue due to: " + e.getMessage());
        }
    }

    private Severity getSeverity(String level) {
        switch (level) {
            case "info": return Severity.INFO;
            case "warning": return Severity.MINOR;
            case "error": return Severity.MAJOR;
            default: return null;
        }
    }

    @Override
    public void execute(SensorContext context) {
        if (!isPluginDisabled(context, PLUGIN_NAME, DISABLED_PROPERTY, DISABLED_PROPERTY_DEFAULT)) {
            LOG.info("Running clj-kondo");
            long timeOut = context.config().getLong(SENSORS_TIMEOUT_PROPERTY)
                    .orElse(Long.valueOf(SENSORS_TIMEOUT_PROPERTY_DEFAULT));

            CommandStreamConsumer stdOut = this.commandRunner.run(timeOut, LEIN_COMMAND, COMMAND);

            List<Finding> issues = KondoIssueParser.parse(stdOut);
            LOG.info("Saving issues " + issues.size());
            for (Finding finding : issues) {
                saveIssue(finding, context);
            }
        }
    }
}
