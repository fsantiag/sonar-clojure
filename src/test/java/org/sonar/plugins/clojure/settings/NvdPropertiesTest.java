package org.sonar.plugins.clojure.settings;

import org.junit.Test;
import org.sonar.api.config.PropertyDefinition;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NvdPropertiesTest {
    @Test
    public void shouldHaveNvdDisabledProperty() {
        PropertyDefinition nvdDisabled = NvdProperties.getDisabledProperty();
        assertThat(nvdDisabled.key(), is("sonar.clojure.nvd.disabled"));
        assertThat(nvdDisabled.name(), is("Lein NVD Disabled"));
        assertThat(nvdDisabled.category(), is("SonarClojure"));
        assertThat(nvdDisabled.subCategory(), is("Sensors"));
        assertThat(nvdDisabled.defaultValue(), is("false"));
        assertThat(nvdDisabled.description(), is("Indicates if lein-nvd sensor should be disabled"));
    }

    @Test
    public void shouldHaveNvdReportLocationProperty() {
        PropertyDefinition nvdReportLocation = NvdProperties.getReportLocationProperty();
        assertThat(nvdReportLocation.key(), is("sonar.clojure.nvd.reportPath"));
        assertThat(nvdReportLocation.name(), is("Lein NVD Report Location"));
        assertThat(nvdReportLocation.category(), is("SonarClojure"));
        assertThat(nvdReportLocation.subCategory(), is("Sensors"));
        assertThat(nvdReportLocation.defaultValue(), is("target/nvd/dependency-check-report.json"));
        assertThat(nvdReportLocation.description(), is("Indicates the location of the Lein NVD report file"));
    }

}