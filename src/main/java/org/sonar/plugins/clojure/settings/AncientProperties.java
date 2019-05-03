package org.sonar.plugins.clojure.settings;

import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.sonar.plugins.clojure.settings.Properties.MAIN_CATEGORY;
import static org.sonar.plugins.clojure.settings.Properties.SUB_CATEGORY;

public class AncientProperties {
    public static final String ANCIENT_DISABLED = "sonar.clojure.ancient.disabled";

    private AncientProperties() {
    }

    static PropertyDefinition getAncientDisabled() {
        return PropertyDefinition.builder(ANCIENT_DISABLED)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue("false")
                .name("Ancient Disabled")
                .description("Indicates the ancient sensor should be disabled")
                .build();
    }

    static List<PropertyDefinition> getProperties() {
        return singletonList(getAncientDisabled());
    }
}
