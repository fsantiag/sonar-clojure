package org.sonar.plugins.clojure.sensors.eastwood;

import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.sonar.plugins.clojure.settings.Properties.MAIN_CATEGORY;
import static org.sonar.plugins.clojure.settings.Properties.SUB_CATEGORY;

public class EastwoodProperties {
    public static final String ENABLED_PROPERTY = "sonar.clojure.eastwood.enabled";
    public static final boolean ENABLED_PROPERTY_DEFAULT = true;
    public static final String EASTWOOD_OPTIONS = "sonar.clojure.eastwood.options";

    private EastwoodProperties() {
    }

    public static PropertyDefinition getEnabledProperty() {
        return PropertyDefinition.builder(ENABLED_PROPERTY)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue(valueOf(ENABLED_PROPERTY_DEFAULT))
                .name("Eastwood Disabled")
                .description("Indicates if eastwood sensor should be disabled")
                .build();
    }

    public static PropertyDefinition getEastwoodOptions() {
        return PropertyDefinition.builder(EASTWOOD_OPTIONS)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue("")
                .name("Eastwood Options")
                .description("Provide options for eastwood plugin (e.g {:continue-on-exception true})")
                .build();
    }

    public static List<PropertyDefinition> getProperties() {
        return asList(getEnabledProperty(), getEastwoodOptions());
    }
}
