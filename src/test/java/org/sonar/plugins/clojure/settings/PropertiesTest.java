package org.sonar.plugins.clojure.settings;

import org.junit.Test;
import org.sonar.api.config.PropertyDefinition;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.sonar.plugins.clojure.settings.Properties.getFileSuffix;
import static org.sonar.plugins.clojure.settings.Properties.getSensorsTimeout;

public class PropertiesTest {

    @Test
    public void shouldGetFileSuffixProperty() {
        PropertyDefinition fileSuffix = getFileSuffix();
        assertThat(fileSuffix.key(), is("sonar.clojure.file.suffixes"));
        assertThat(fileSuffix.name(), is("File Suffixes"));
        assertThat(fileSuffix.category(), is("SonarClojure"));
        assertThat(fileSuffix.defaultValue(), is("clj,cljs,cljc"));
        assertThat(fileSuffix.description(), is("Comma-separated list of file suffixes to analyze"));
    }

    @Test
    public void shouldGetSensorTimeout() {
        PropertyDefinition fileSuffix = getSensorsTimeout();
        assertThat(fileSuffix.key(), is("sonar.clojure.sensors.timeout"));
        assertThat(fileSuffix.name(), is("Sensors Timeout"));
        assertThat(fileSuffix.category(), is("SonarClojure"));
        assertThat(fileSuffix.defaultValue(), is("300"));
        assertThat(fileSuffix.description(),
                is("Defines the maximum timeout (per sensor, in seconds) when sensors are executing"));
    }
}