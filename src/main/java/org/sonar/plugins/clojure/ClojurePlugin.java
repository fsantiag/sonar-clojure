package org.sonar.plugins.clojure;


import org.sonar.api.Plugin;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.language.ClojureLanguage;
import org.sonar.plugins.clojure.language.ClojureSonarWayProfile;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;
import org.sonar.plugins.clojure.sensors.eastwood.EastwoodSensor;
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.settings.ClojureProperties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ClojurePlugin implements Plugin {

    private static final Logger LOG = Loggers.get(SonarProjectProperties.class);
    private final SonarProjectProperties props = new SonarProjectProperties();

    private void addExtensionIfNotDisabled(Class extension, Context context){
        if (!props.isSensorDisabled(extension)) {
            context.addExtension(extension);
        }
    }

    public void define(Context context) {

        try {
            props.initialize(new FileInputStream("sonar-project.properties"));
        } catch (FileNotFoundException e) {
            LOG.info("sonar-project.properties does not seems to exist");
        }

        context.addExtension(ClojureProperties.getProperties());
        context.addExtension(ClojureLanguage.class);
        context.addExtension(ClojureSonarWayProfile.class);
        context.addExtension(ClojureLintRulesDefinition.class);
        context.addExtension(CommandRunner.class);
        addExtensionIfNotDisabled(EastwoodSensor.class, context);
    }
}