package org.sonar.plugins.clojure.settings;

import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.sonar.plugins.clojure.settings.Properties.MAIN_CATEGORY;
import static org.sonar.plugins.clojure.settings.Properties.SUB_CATEGORY;

public class KibitProperties {
    public static final String KIBIT_DISABLED = "sonar.clojure.kibit.disabled";

    static PropertyDefinition getKibitDisabled() {
        return PropertyDefinition.builder(KIBIT_DISABLED)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue("false")
                .name("Kibit Disabled")
                .description("Indicates if kibit sensor should be disabled")
                .build();
    }

    static List<PropertyDefinition> getProperties() {
        return singletonList(getKibitDisabled());
    }
}
