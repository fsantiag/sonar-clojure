package org.sonar.plugins.clojure.settings;

import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.sonar.plugins.clojure.settings.Properties.MAIN_CATEGORY;
import static org.sonar.plugins.clojure.settings.Properties.SUB_CATEGORY;

public class KondoProperties {
    public static final String DISABLED_PROPERTY = "sonar.clojure.kondo.disabled";
    public static final String OPTIONS = "sonar.clojure.kondo.options";
    public static final String CONFIG = "sonar.clojure.kondo.config";
    public static final boolean DISABLED_PROPERTY_DEFAULT = true;

    private KondoProperties() {
    }

    static PropertyDefinition getDisabledProperty() {
        return PropertyDefinition.builder(DISABLED_PROPERTY)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue(valueOf(DISABLED_PROPERTY_DEFAULT))
                .name("clj-kondo disabled")
                .description("Indicates if clj-kondo sensor should be disabled")
                .build();
    }

    static PropertyDefinition getOptionsProperty() {
        return PropertyDefinition.builder(OPTIONS)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue("--lint src")
                .name("clj-kondo options")
                .description("Provide options for clj-kondo plugin (e.g --lint src)")
                .build();
    }

    static PropertyDefinition getConfigProperty() {
        return PropertyDefinition.builder(CONFIG)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue("{:output {:format :edn}}")
                .name("clj-kondo config")
                .description("Provide config for clj-kondo plugin (e.g {:output {:format :edn}})")
                .build();
    }

    static List<PropertyDefinition> getProperties() {
        return asList(getDisabledProperty(), getOptionsProperty(), getConfigProperty());
    }
}