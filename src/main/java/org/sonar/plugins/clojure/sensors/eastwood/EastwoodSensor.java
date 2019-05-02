package org.sonar.plugins.clojure.sensors.eastwood;


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
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.sensors.Issue;
import org.sonar.plugins.clojure.settings.ClojureProperties;

import java.util.List;

public class EastwoodSensor extends AbstractSensor implements Sensor {

    private static final Logger LOG = Loggers.get(EastwoodSensor.class);

    private static final String EASTWOOD_COMMAND = "eastwood";

    public EastwoodSensor(CommandRunner commandRunner) {
        super(commandRunner);
    }


    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("SonarClojure")
                .onlyOnLanguage(ClojureLanguage.KEY)
                .global();
    }

    @Override
    public void execute(SensorContext context) {
        if (!checkIfPluginIsDisabled(context, ClojureProperties.EASTWOOD_DISABLED)) {
            LOG.info("Clojure project detected");
            LOG.info("Running Eastwood");
            String options = context.config().get(ClojureProperties.EASTWOOD_OPTIONS).orElse(null);
            CommandStreamConsumer stdOut = this.commandRunner.run(LEIN_COMMAND, EASTWOOD_COMMAND, options);
            if (isLeinInstalled(stdOut.getData()) && isPluginInstalled(stdOut.getData(), EASTWOOD_COMMAND)){
                String info = EastwoodIssueParser.parseRuntimeInfo(stdOut);
                if (info != null) {
                    LOG.info("Ran " + info);
                } else {
                    LOG.warn("Eastwood resulted in empty output");
                }

                List<Issue> issues = EastwoodIssueParser.parse(stdOut);
                LOG.info("Saving issues " + issues.size());
                for (Issue issue : issues) {
                    saveIssue(issue, context);
                }
            }
        } else {
            LOG.info ("Eastwood plugin is disabled");
        }
    }
}