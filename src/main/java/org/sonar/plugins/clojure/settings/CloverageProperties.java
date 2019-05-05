package org.sonar.plugins.clojure.settings;

import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static java.util.Arrays.asList;
import static org.sonar.plugins.clojure.settings.Properties.MAIN_CATEGORY;
import static org.sonar.plugins.clojure.settings.Properties.SUB_CATEGORY;

public class CloverageProperties {
    public static final String DISABLED_PROPERTY = "sonar.clojure.cloverage.disabled";
    public static final boolean DISABLED_PROPERTY_DEFAULT = false;
    public static final String REPORT_LOCATION_PROPERTY = "sonar.clojure.cloverage.reportPath";
    public static final String REPORT_LOCATION_DEFAULT = "target/coverage/codecov.json";

    private CloverageProperties() {
    }

    static PropertyDefinition getDisabledProperty() {
        return PropertyDefinition.builder(DISABLED_PROPERTY)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue("false")
                .name("Cloverage Disabled")
                .description("Indicates if cloverage sensor should be disabled")
                .build();
    }

    static PropertyDefinition getReportLocationProperty() {
        return PropertyDefinition.builder(REPORT_LOCATION_PROPERTY)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue(REPORT_LOCATION_DEFAULT)
                .name("Cloverage Report Location")
                .description("Indicates the location of the cloverage report file")
                .build();
    }

    static List<PropertyDefinition> getProperties() {
        return asList(getDisabledProperty(), getReportLocationProperty());
    }
}
