package org.sonar.plugins.clojure.settings;

import org.junit.Test;
import org.sonar.api.config.PropertyDefinition;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CloveragePropertiesTest {
    @Test
    public void shouldHaveCloverageDisabledProperty() {
        PropertyDefinition cloverageDisabled = CloverageProperties.getCloverageDisabled();
        assertThat(cloverageDisabled.key(), is("sonar.clojure.cloverage.disabled"));
        assertThat(cloverageDisabled.name(), is("Cloverage Disabled"));
        assertThat(cloverageDisabled.category(), is("SonarClojure"));
        assertThat(cloverageDisabled.subCategory(), is("Sensors"));
        assertThat(cloverageDisabled.defaultValue(), is("false"));
        assertThat(cloverageDisabled.description(), is("Indicates if cloverage sensor should be disabled"));
    }

    @Test
    public void shouldHaveCloverageReportLocationProperty() {
        PropertyDefinition cloverageReportLocation = CloverageProperties.getCloverageReportLocation();
        assertThat(cloverageReportLocation.key(), is("sonar.clojure.cloverage.reportPath"));
        assertThat(cloverageReportLocation.name(), is("Cloverage Report Location"));
        assertThat(cloverageReportLocation.category(), is("SonarClojure"));
        assertThat(cloverageReportLocation.subCategory(), is("Sensors"));
        assertThat(cloverageReportLocation.defaultValue(), is("target/coverage/codecov.json"));
        assertThat(cloverageReportLocation.description(), is("Indicates the location of the cloverage report file"));
    }

}