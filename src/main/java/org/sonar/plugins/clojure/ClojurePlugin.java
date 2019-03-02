package org.sonar.plugins.clojure;

import org.sonar.api.Plugin;
import org.sonar.plugins.clojure.language.ClojureLanguage;
import org.sonar.plugins.clojure.language.ClojureSonarWayProfile;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;
import org.sonar.plugins.clojure.sensors.cloverage.CloverageSensor;
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.sensors.ancient.AncientSensor;
import org.sonar.plugins.clojure.sensors.eastwood.EastwoodSensor;
import org.sonar.plugins.clojure.sensors.kibit.KibitSensor;
import org.sonar.plugins.clojure.sensors.leinnvd.LeinNvdSensor;
import org.sonar.plugins.clojure.settings.ClojureProperties;

public class ClojurePlugin implements Plugin {

    public void define(Context context) {
        context.addExtension(ClojureProperties.getProperties());
        context.addExtension(ClojureLanguage.class);
        context.addExtension(ClojureSonarWayProfile.class);
        context.addExtension(ClojureLintRulesDefinition.class);
        context.addExtension(CommandRunner.class);
        context.addExtension(EastwoodSensor.class);
        context.addExtension(KibitSensor.class);
        context.addExtension(AncientSensor.class);
        context.addExtension(CloverageSensor.class);
        context.addExtension(LeinNvdSensor.class);
    }
}