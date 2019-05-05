package org.sonar.plugins.clojure.settings;

import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Collections.singletonList;
import static org.sonar.plugins.clojure.settings.Properties.MAIN_CATEGORY;
import static org.sonar.plugins.clojure.settings.Properties.SUB_CATEGORY;

public class KibitProperties {
    public static final String DISABLED_PROPERTY = "sonar.clojure.kibit.disabled";
    public static final boolean DISABLED_PROPERTY_DEFAULT = false;

    private KibitProperties() {
    }

    static PropertyDefinition getDisabledProperty() {
        return PropertyDefinition.builder(DISABLED_PROPERTY)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue(valueOf(DISABLED_PROPERTY_DEFAULT))
                .name("Kibit Disabled")
                .description("Indicates if kibit sensor should be disabled")
                .build();
    }

    static List<PropertyDefinition> getProperties() {
        return singletonList(getDisabledProperty());
    }
}
