package org.sonar.plugins.clojure.settings;

import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.sonar.plugins.clojure.settings.Properties.MAIN_CATEGORY;
import static org.sonar.plugins.clojure.settings.Properties.SUB_CATEGORY;

public class EastwoodProperties {
    public static final String DISABLED_PROPERTY = "sonar.clojure.eastwood.disabled";
    public static final boolean DISABLED_PROPERTY_DEFAULT = false;
    public static final String EASTWOOD_OPTIONS = "sonar.clojure.eastwood.options";

    private EastwoodProperties() {
    }

    static PropertyDefinition getDisabledProperty() {
        return PropertyDefinition.builder(DISABLED_PROPERTY)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue(valueOf(DISABLED_PROPERTY_DEFAULT))
                .name("Eastwood Disabled")
                .description("Indicates if eastwood sensor should be disabled")
                .build();
    }

    static PropertyDefinition getEastwoodOptions() {
        return PropertyDefinition.builder(EASTWOOD_OPTIONS)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue("")
                .name("Eastwood Options")
                .description("Provide options for eastwood plugin (e.g {:continue-on-exception true})")
                .build();
    }

    static List<PropertyDefinition> getProperties() {
        return asList(getDisabledProperty(), getEastwoodOptions());
    }
}
