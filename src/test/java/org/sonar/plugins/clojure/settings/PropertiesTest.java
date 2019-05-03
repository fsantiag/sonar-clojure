package org.sonar.plugins.clojure.settings;

import org.junit.Test;
import org.sonar.api.config.PropertyDefinition;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PropertiesTest {

    @Test
    public void shouldGetFileSuffixProperty() {
        PropertyDefinition fileSuffix = Properties.getFileSuffix();
        assertThat(fileSuffix.key(), is("sonar.clojure.file.suffixes"));
        assertThat(fileSuffix.name(), is("File Suffixes"));
        assertThat(fileSuffix.category(), is("SonarClojure"));
        assertThat(fileSuffix.defaultValue(), is("clj,cljs,cljc"));
        assertThat(fileSuffix.description(), is("Comma-separated list of file suffixes to analyze"));
    }
}