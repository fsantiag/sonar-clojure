package org.sonar.plugins.clojure;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SonarProjectProperties {

    private static final Logger LOG = Loggers.get(SonarProjectProperties.class);
    private Properties prop = new Properties();
    private Map<String,String> classToPropertyName = new HashMap<>();

    public SonarProjectProperties(){
        classToPropertyName.put("org.sonar.plugins.clojure.sensors.eastwood.EastwoodSensor", "clojure.disable.eastwood");
        classToPropertyName.put("org.sonar.plugins.clojure.sensors.kibit.KibitSensor", "clojure.disable.kibit");
        classToPropertyName.put("org.sonar.plugins.clojure.sensors.ancient.AncientSensor", "clojure.disable.ancient");
        classToPropertyName.put("org.sonar.plugins.clojure.sensors.cloverage.CloverageSensor", "clojure.disable.cloverage");
        classToPropertyName.put("org.sonar.plugins.clojure.sensors.leinNvd.LeinNvdSensor", "clojure.disable.leinnvd");
    }

    public void initialize(FileInputStream sonarProjectPropertiesFile){
        try (FileInputStream input = sonarProjectPropertiesFile) {
            LOG.info("Reading properties from sonar-project.properties");
            prop.load(input);
        } catch (IOException e) {
            LOG.info("Properties read failed - does sonar-project.properties file exist?");
        }
    }
    public boolean isSensorDisabled(Class sensor ){

        if (classToPropertyName.containsKey(sensor.getName())){
            String value =  classToPropertyName.get(sensor.getName());
            LOG.debug(sensor.getName() + " mapped to: " + value);
            String property = prop.getProperty(value);
            LOG.debug("Value from property: " + property);
            return Boolean.parseBoolean(property);
        } else {
            LOG.info ("Unknown property for: " + sensor.getName());
            return false;
        }

    }

}

