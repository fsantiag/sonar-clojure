package org.sonar.plugins.clojure.settings;

import org.junit.Test;
import org.sonar.api.config.PropertyDefinition;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AncientPropertiesTest {
    @Test
    public void shouldHaveAncientDisabledProperty() {
        PropertyDefinition ancientDisabled = AncientProperties.getAncientDisabled();
        assertThat(ancientDisabled.key(), is("sonar.clojure.ancient.disabled"));
        assertThat(ancientDisabled.name(), is("Ancient Disabled"));
        assertThat(ancientDisabled.category(), is("SonarClojure"));
        assertThat(ancientDisabled.subCategory(), is("Sensors"));
        assertThat(ancientDisabled.defaultValue(), is("false"));
        assertThat(ancientDisabled.description(), is("Indicates the ancient sensor should be disabled"));
    }

}