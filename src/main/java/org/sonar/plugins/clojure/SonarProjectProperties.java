package org.sonar.plugins.clojure;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.sensors.leinNvd.LeinNvdParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SonarProjectProperties {

    private static final Logger LOG = Loggers.get(SonarProjectProperties.class);
    private Properties prop = new Properties();
    private boolean isKibitDisabled = false;
    private boolean isEastwoodDisabled = false;
    private boolean isCloverageDisabled = false;
    private boolean isLeinNVDDisabled = false;

    public boolean isKibitDisabled() {
        return isKibitDisabled;
    }

    public boolean isEastwoodDisabled() {
        return isEastwoodDisabled;
    }

    public boolean isCloverageDisabled() {
        return isCloverageDisabled;
    }

    public boolean isLeinNVDDisabled() {
        return isLeinNVDDisabled;
    }

    public SonarProjectProperties() {
        try (FileInputStream input = new FileInputStream("sonar-project.properties")) {
            // load a properties file
            prop.load(input);
            isKibitDisabled = Boolean.parseBoolean(prop.getProperty("clojure.disable.kibit"));
            isEastwoodDisabled = Boolean.parseBoolean(prop.getProperty("clojure.disable.eastwood"));
            isCloverageDisabled = Boolean.parseBoolean(prop.getProperty("clojure.disable.cloverage"));
            isLeinNVDDisabled = Boolean.parseBoolean(prop.getProperty("clojure.disable.leinnvd"));
        } catch (IOException e) {
            LOG.warn("Properties read failed");
        }

    }
}

