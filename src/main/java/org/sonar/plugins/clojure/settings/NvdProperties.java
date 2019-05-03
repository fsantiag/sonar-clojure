package org.sonar.plugins.clojure.settings;

import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static java.util.Arrays.asList;
import static org.sonar.plugins.clojure.settings.Properties.MAIN_CATEGORY;
import static org.sonar.plugins.clojure.settings.Properties.SUB_CATEGORY;

public class NvdProperties {
    public static final String NVD_DISABLED = "sonar.clojure.nvd.disabled";
    public static final String NVD_REPORT_LOCATION = "sonar.clojure.nvd.reportPath";


    static PropertyDefinition getNvdDisabled() {
        return PropertyDefinition.builder(NVD_DISABLED)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue("false")
                .name("Lein NVD Disabled")
                .description("Indicates if lein-nvd sensor should be disabled")
                .build();
    }

    static PropertyDefinition getNvdReportLocation() {
        return PropertyDefinition.builder(NVD_REPORT_LOCATION)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue("src/test/resources/nvd-report.json")
                .name("Lein NVD Report Location")
                .description("Indicates the location of the Lein NVD report file")
                .build();
    }

    static List<PropertyDefinition> getProperties() {
        return asList(getNvdDisabled(), getNvdReportLocation());
    }
}
