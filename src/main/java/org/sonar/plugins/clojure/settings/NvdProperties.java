package org.sonar.plugins.clojure.settings;

import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.sonar.plugins.clojure.settings.Properties.MAIN_CATEGORY;
import static org.sonar.plugins.clojure.settings.Properties.SUB_CATEGORY;

public class NvdProperties {
    public static final String ENABLED_PROPERTY = "sonar.clojure.nvd.enabled";
    public static final boolean ENABLED_PROPERTY_DEFAULT = true;
    public static final String REPORT_LOCATION_PROPERTY = "sonar.clojure.nvd.reportPath";
    public static final String REPORT_LOCATION_DEFAULT = "target/nvd/dependency-check-report.json";

    private NvdProperties() {
    }

    static PropertyDefinition getEnabledProperty() {
        return PropertyDefinition.builder(ENABLED_PROPERTY)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue(valueOf(ENABLED_PROPERTY_DEFAULT))
                .name("Lein NVD Disabled")
                .description("Indicates if lein-nvd sensor should be disabled")
                .build();
    }

    static PropertyDefinition getReportLocationProperty() {
        return PropertyDefinition.builder(REPORT_LOCATION_PROPERTY)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue(REPORT_LOCATION_DEFAULT)
                .name("Lein NVD Report Location")
                .description("Indicates the location of the Lein NVD report file")
                .build();
    }

    static List<PropertyDefinition> getProperties() {
        return asList(getEnabledProperty(), getReportLocationProperty());
    }
}
