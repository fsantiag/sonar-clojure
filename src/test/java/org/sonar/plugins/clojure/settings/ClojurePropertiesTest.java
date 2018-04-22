package org.sonar.plugins.clojure.settings;

import org.junit.Test;
import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClojurePropertiesTest {

    @Test
    public void testGetFileSuffixProperty() {
        PropertyDefinition fileSuffixProperty = ClojureProperties.getFileSuffixProperty();
        assertThat(fileSuffixProperty.key(), is("sonar.clojure.file.suffixes"));
        assertThat(fileSuffixProperty.name(), is("File Suffixes"));
        assertThat(fileSuffixProperty.category(), is("ClojureLanguage"));
        assertThat(fileSuffixProperty.defaultValue(), is("clj,cljs,cljc"));
        assertThat(fileSuffixProperty.description(), is("Comma-separated list of suffixes for files to analyze."));
    }

    @Test
    public void testGetProperties() {
        List<PropertyDefinition> propertyDefinitions = ClojureProperties.getProperties();
        assertThat(propertyDefinitions.size(), is(1));
        assertThat(propertyDefinitions.get(0).key(), is("sonar.clojure.file.suffixes"));

    }
}