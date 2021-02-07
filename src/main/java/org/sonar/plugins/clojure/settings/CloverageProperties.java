package org.sonar.plugins.clojure.settings;

import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static java.util.Arrays.asList;
import static org.sonar.plugins.clojure.settings.Properties.MAIN_CATEGORY;
import static org.sonar.plugins.clojure.settings.Properties.SUB_CATEGORY;

public class CloverageProperties {
    public static final String ENABLED_PROPERTY = "sonar.clojure.cloverage.enabled";
    public static final boolean ENABLED_PROPERTY_DEFAULT = true;
    public static final String REPORT_LOCATION_PROPERTY = "sonar.clojure.cloverage.reportPath";
    public static final String REPORT_LOCATION_DEFAULT = "target/coverage/codecov.json";

    private CloverageProperties() {
    }

    static PropertyDefinition getEnabledProperty() {
        return PropertyDefinition.builder(ENABLED_PROPERTY)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue(String.valueOf(ENABLED_PROPERTY_DEFAULT))
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
        return asList(getEnabledProperty(), getReportLocationProperty());
    }
}
