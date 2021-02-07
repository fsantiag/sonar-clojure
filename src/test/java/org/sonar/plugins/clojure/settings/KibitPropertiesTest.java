package org.sonar.plugins.clojure.settings;

import org.junit.Test;
import org.sonar.api.config.PropertyDefinition;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class KibitPropertiesTest {
    @Test
    public void shouldHaveKibitDisabledProperty() {
        PropertyDefinition kibitDisabled = KibitProperties.getEnabledProperty();
        assertThat(kibitDisabled.key(), is("sonar.clojure.kibit.enabled"));
        assertThat(kibitDisabled.name(), is("Kibit Disabled"));
        assertThat(kibitDisabled.category(), is("SonarClojure"));
        assertThat(kibitDisabled.subCategory(), is("Sensors"));
        assertThat(kibitDisabled.defaultValue(), is("true"));
        assertThat(kibitDisabled.description(), is("Indicates if kibit sensor should be disabled"));
    }

}