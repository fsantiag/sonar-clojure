package org.sonar.plugins.clojure.settings;

import org.junit.Test;
import org.sonar.api.config.PropertyDefinition;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EastwoodPropertiesTest {
    @Test
    public void shouldHaveEastwoodDisabledProperty() {
        PropertyDefinition eastwoodDisabled = EastwoodProperties.getDisabledProperty();
        assertThat(eastwoodDisabled.key(), is("sonar.clojure.eastwood.disabled"));
        assertThat(eastwoodDisabled.name(), is("Eastwood Disabled"));
        assertThat(eastwoodDisabled.category(), is("SonarClojure"));
        assertThat(eastwoodDisabled.subCategory(), is("Sensors"));
        assertThat(eastwoodDisabled.defaultValue(), is("false"));
        assertThat(eastwoodDisabled.description(), is("Indicates if eastwood sensor should be disabled"));
    }

    @Test
    public void shouldHaveEastwoodOptionsProperty() {
        PropertyDefinition eastwoodOptions = EastwoodProperties.getEastwoodOptions();
        assertThat(eastwoodOptions.key(), is("sonar.clojure.eastwood.options"));
        assertThat(eastwoodOptions.name(), is("Eastwood Options"));
        assertThat(eastwoodOptions.category(), is("SonarClojure"));
        assertThat(eastwoodOptions.subCategory(), is("Sensors"));
        assertThat(eastwoodOptions.defaultValue(), is(""));
        assertThat(eastwoodOptions.description(), is("Provide options for eastwood plugin (e.g {:continue-on-exception true})"));
    }

}