package org.sonar.plugins.clojure.sensors.ancient;


import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.language.Clojure;
import org.sonar.plugins.clojure.leiningen.ProjectFile;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;
import org.sonar.plugins.clojure.sensors.AbstractSensor;
import org.sonar.plugins.clojure.sensors.LeiningenRunner;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.sonar.plugins.clojure.sensors.ancient.AncientOutputParser.parse;
import static org.sonar.plugins.clojure.settings.AncientProperties.ENABLED_PROPERTY;
import static org.sonar.plugins.clojure.settings.AncientProperties.ENABLED_PROPERTY_DEFAULT;
import static org.sonar.plugins.clojure.settings.Properties.SENSORS_TIMEOUT_PROPERTY;
import static org.sonar.plugins.clojure.settings.Properties.SENSORS_TIMEOUT_PROPERTY_DEFAULT;

public class AncientSensor extends AbstractSensor implements Sensor {

    private static final Logger LOG = Loggers.get(AncientSensor.class);

    private static final String LEIN_ARGUMENTS = "ancient";
    private static final String PLUGIN_NAME = "Ancient";

    @SuppressWarnings("WeakerAccess")
    public AncientSensor(LeiningenRunner leiningenRunner) {
        super(leiningenRunner);
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name(PLUGIN_NAME)
                .onlyOnLanguage(Clojure.KEY);
    }

    @Override
    public void execute(SensorContext context) {
        if (isPluginEnabled(context, PLUGIN_NAME, ENABLED_PROPERTY, ENABLED_PROPERTY_DEFAULT)) {
            LOG.info("Running Lein Ancient");

            long timeOut = context.config().getLong(SENSORS_TIMEOUT_PROPERTY)
                    .orElse(Long.valueOf(SENSORS_TIMEOUT_PROPERTY_DEFAULT));

            CommandStreamConsumer stdOut = this.leiningenRunner.run(timeOut, LEIN_ARGUMENTS);

            List<OutdatedDependency> outdatedDependencies = parse(stdOut.getData());
            LOG.debug("Parsed " + outdatedDependencies.size() + " dependencies");
            saveOutdated(outdatedDependencies, context);
        }
    }

    private void saveOutdated(List<OutdatedDependency> outdatedDependencies, SensorContext context) {

        Optional<InputFile> fileOptional = getFile("project.clj", context.fileSystem());

        fileOptional.ifPresent(projectFile -> outdatedDependencies.forEach(outdatedDependency -> {
            ProjectFile pr;
            try {
                pr = new ProjectFile(projectFile.contents());
            } catch (IOException e) {
                LOG.warn("project.clj could not be read");
                return;
            }
            LOG.debug("Processing outdated dependencies");

            RuleKey ruleKey = RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, "ancient-clj-dependency");
            NewIssue newIssue = context.newIssue().forRule(ruleKey);
            int lineNumber = pr.findLineNumber(
                    outdatedDependency.getName() + " \"" + outdatedDependency.getCurrentVersion() + "\"");

            NewIssueLocation primaryLocation = newIssue
                    .newLocation()
                    .on(projectFile)
                    .message(outdatedDependency.toString())
                    .at(projectFile.selectLine(lineNumber));
            newIssue.at(primaryLocation);
            newIssue.save();
        }));
    }

}