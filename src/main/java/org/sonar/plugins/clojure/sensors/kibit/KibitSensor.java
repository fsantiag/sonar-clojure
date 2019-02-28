package org.sonar.plugins.clojure.sensors.kibit;


import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;

import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.language.ClojureLanguage;

import org.sonar.plugins.clojure.sensors.AbstractSensor;
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.sensors.Issue;
import org.sonar.plugins.clojure.settings.ClojureProperties;

import java.util.List;

public class KibitSensor extends AbstractSensor implements Sensor {

    private static final Logger LOG = Loggers.get(KibitSensor.class);

    private static final String KIBIT_COMMAND = "kibit";

    public KibitSensor(CommandRunner commandRunner) { super(commandRunner); }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("SonarClojureKibit")
                .onlyOnLanguage(ClojureLanguage.KEY)
                .global();
    }

    @Override
    public void execute(SensorContext context) {
        LOG.info("Running Kibit");
        CommandStreamConsumer stdOut = this.commandRunner.run(LEIN_COMMAND, KIBIT_COMMAND);
        if (!checkIfPluginIsDisabled(context, ClojureProperties.KIBIT_DISABLED)) {
            if (isLeinInstalled(stdOut.getData()) && isPluginInstalled(stdOut.getData(), KIBIT_COMMAND)){

                List<Issue> issues = KibitIssueParser.parse(stdOut);
                LOG.info("Saving issues");
                for (Issue issue : issues) {
                    saveIssue(issue, context);
                }
            } else {
                LOG.warn("Parsing skipped because Leiningen or Kibit are not installed");
            }
        } else {
            LOG.info ("Kibit sensor is disabled");
        }
    }

}