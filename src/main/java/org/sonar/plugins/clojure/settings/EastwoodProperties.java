package org.sonar.plugins.clojure.settings;

import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static java.util.Arrays.asList;
import static org.sonar.plugins.clojure.settings.Properties.MAIN_CATEGORY;
import static org.sonar.plugins.clojure.settings.Properties.SUB_CATEGORY;

public class EastwoodProperties {
    public static final String EASTWOOD_DISABLED = "sonar.clojure.eastwood.disabled";
    public static final String EASTWOOD_OPTIONS = "sonar.clojure.eastwood.options";

    private EastwoodProperties() {
    }

    static PropertyDefinition getEastwoodDisabled() {
        return PropertyDefinition.builder(EASTWOOD_DISABLED)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue("false")
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
        return asList(getEastwoodDisabled(), getEastwoodOptions());
    }
}
