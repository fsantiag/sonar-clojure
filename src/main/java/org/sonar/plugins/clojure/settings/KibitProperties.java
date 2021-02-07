package org.sonar.plugins.clojure.settings;

import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Collections.singletonList;
import static org.sonar.plugins.clojure.settings.Properties.MAIN_CATEGORY;
import static org.sonar.plugins.clojure.settings.Properties.SUB_CATEGORY;

public class KibitProperties {
    public static final String ENABLED_PROPERTY = "sonar.clojure.kibit.enabled";
    public static final boolean ENABLED_PROPERTY_DEFAULT = true;

    private KibitProperties() {
    }

    static PropertyDefinition getEnabledProperty() {
        return PropertyDefinition.builder(ENABLED_PROPERTY)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue(valueOf(ENABLED_PROPERTY_DEFAULT))
                .name("Kibit Disabled")
                .description("Indicates if kibit sensor should be disabled")
                .build();
    }

    static List<PropertyDefinition> getProperties() {
        return singletonList(getEnabledProperty());
    }
}
