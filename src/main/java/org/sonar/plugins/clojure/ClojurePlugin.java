package org.sonar.plugins.clojure;

import org.sonar.api.Plugin;
import org.sonar.plugins.clojure.language.ClojureLanguage;
import org.sonar.plugins.clojure.language.ClojureSonarWayProfile;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.sensors.ancient.AncientSensor;
import org.sonar.plugins.clojure.sensors.cloverage.CloverageSensor;
import org.sonar.plugins.clojure.sensors.eastwood.EastwoodSensor;
import org.sonar.plugins.clojure.sensors.kibit.KibitSensor;
import org.sonar.plugins.clojure.sensors.kondo.KondoSensor;
import org.sonar.plugins.clojure.sensors.leinnvd.LeinNvdSensor;
import org.sonar.plugins.clojure.settings.Properties;

public class ClojurePlugin implements Plugin {

    @Override
    public void define(Context context) {
        context.addExtension(Properties.getAllProperties());
        context.addExtension(ClojureLanguage.class);
        context.addExtension(ClojureSonarWayProfile.class);
        context.addExtension(ClojureLintRulesDefinition.class);
        context.addExtension(CommandRunner.class);
        context.addExtension(EastwoodSensor.class);
        context.addExtension(KibitSensor.class);
        context.addExtension(AncientSensor.class);
        context.addExtension(CloverageSensor.class);
        context.addExtension(LeinNvdSensor.class);
        context.addExtension(KondoSensor.class);
    }
}