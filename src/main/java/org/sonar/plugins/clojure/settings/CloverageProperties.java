package org.sonar.plugins.clojure.settings;

import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static java.util.Arrays.asList;
import static org.sonar.plugins.clojure.settings.Properties.MAIN_CATEGORY;
import static org.sonar.plugins.clojure.settings.Properties.SUB_CATEGORY;

public class CloverageProperties {
    public static final String CLOVERAGE_DISABLED = "sonar.clojure.cloverage.disabled";
    public static final String CLOVERAGE_REPORT_LOCATION = "sonar.clojure.cloverage.reportPath";

    private CloverageProperties() {
    }

    static PropertyDefinition getCloverageDisabled() {
        return PropertyDefinition.builder(CLOVERAGE_DISABLED)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue("false")
                .name("Cloverage Disabled")
                .description("Indicates if cloverage sensor should be disabled")
                .build();
    }

    static PropertyDefinition getCloverageReportLocation() {
        return PropertyDefinition.builder(CLOVERAGE_REPORT_LOCATION)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue("target/coverage/codecov.json")
                .name("Cloverage Report Location")
                .description("Indicates the location of the cloverage report file")
                .build();
    }

    static List<PropertyDefinition> getProperties() {
        return asList(getCloverageDisabled(), getCloverageReportLocation());
    }
}
