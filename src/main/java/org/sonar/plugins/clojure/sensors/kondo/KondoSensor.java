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
import org.sonar.plugins.clojure.language.Clojure;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;
import org.sonar.plugins.clojure.sensors.AbstractSensor;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.sensors.LeiningenRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.sonar.plugins.clojure.settings.KondoProperties.*;
import static org.sonar.plugins.clojure.settings.Properties.SENSORS_TIMEOUT_PROPERTY;
import static org.sonar.plugins.clojure.settings.Properties.SENSORS_TIMEOUT_PROPERTY_DEFAULT;

public class KondoSensor extends AbstractSensor implements Sensor {

    private static final Logger LOG = Loggers.get(KondoSensor.class);

    private static final String PLUGIN_NAME = "clj-kondo";
    private static final String[] KONDO_ARGS = {"run", "-m", "clj-kondo.main"};

    public KondoSensor(LeiningenRunner leiningenRunner) {
        super(leiningenRunner);
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name(PLUGIN_NAME)
                .onlyOnLanguage(Clojure.KEY)
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

                TextRange range = file.newRange(finding.getRow(), finding.getCol() - 1,
                        finding.getEndRow(), finding.getEndCol() - 1);
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
        if (isPluginEnabled(context, PLUGIN_NAME, ENABLED_PROPERTY, ENABLED_PROPERTY_DEFAULT)) {
            LOG.info("Running clj-kondo");

            String config = context.config().get(CONFIG).orElse(DEFAULT_CONFIG);
            String[] options = context.config().get(OPTIONS).orElse(DEFAULT_OPTIONS).split("\\s+");
            List<String> commandAsList = new ArrayList(Arrays.asList(KONDO_ARGS));
            commandAsList.addAll(Arrays.asList(options));
            if (config != null && !config.isEmpty()) {
                commandAsList.add("--config");
                commandAsList.add(config);
            }
            String[] command = commandAsList.toArray(new String[0]);

            long timeOut = context.config().getLong(SENSORS_TIMEOUT_PROPERTY)
                    .orElse(Long.valueOf(SENSORS_TIMEOUT_PROPERTY_DEFAULT));

            CommandStreamConsumer stdOut = this.leiningenRunner.run(timeOut, command);

            List<Finding> issues = KondoIssueParser.parse(stdOut);
            LOG.info("Saving issues " + issues.size());
            for (Finding finding : issues) {
                saveIssue(finding, context);
            }
        }
    }
}
