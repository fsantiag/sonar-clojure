package org.sonar.plugins.clojure;


import org.sonar.api.Plugin;
import org.sonar.plugins.clojure.language.ClojureLanguage;
import org.sonar.plugins.clojure.language.ClojureQualityProfile;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;
import org.sonar.plugins.clojure.sensors.EastwoodSensor;
import org.sonar.plugins.clojure.settings.ClojureProperties;

public class ClojurePlugin implements Plugin {

    public void define(Context context) {
        context.addExtension(ClojureLanguage.class);
        context.addExtension(ClojureQualityProfile.class);
        context.addExtension(ClojureLintRulesDefinition.class);
        context.addExtension(EastwoodSensor.class);
        context.addExtension(ClojureProperties.getProperties());

    }
}