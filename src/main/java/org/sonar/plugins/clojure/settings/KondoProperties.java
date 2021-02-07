package org.sonar.plugins.clojure.settings;

import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.sonar.plugins.clojure.settings.Properties.MAIN_CATEGORY;
import static org.sonar.plugins.clojure.settings.Properties.SUB_CATEGORY;

public class KondoProperties {
    public static final String ENABLED_PROPERTY = "sonar.clojure.kondo.enabled";
    public static final String OPTIONS = "sonar.clojure.kondo.options";
    public static final String CONFIG = "sonar.clojure.kondo.config";
    public static final boolean ENABLED_PROPERTY_DEFAULT = false;
    public static final String DEFAULT_OPTIONS = "--lint src";
    public static final String DEFAULT_CONFIG = "{:output {:format :edn}}";

    private KondoProperties() {
    }

    static PropertyDefinition getEnabledProperty() {
        return PropertyDefinition.builder(ENABLED_PROPERTY)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue(valueOf(ENABLED_PROPERTY_DEFAULT))
                .name("clj-kondo disabled")
                .description("Indicates if clj-kondo sensor should be disabled")
                .build();
    }

    static PropertyDefinition getOptionsProperty() {
        return PropertyDefinition.builder(OPTIONS)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue(DEFAULT_OPTIONS)
                .name("clj-kondo options")
                .description("Provide options for clj-kondo plugin (e.g --lint src)")
                .build();
    }

    static PropertyDefinition getConfigProperty() {
        return PropertyDefinition.builder(CONFIG)
                .category(MAIN_CATEGORY)
                .subCategory(SUB_CATEGORY)
                .defaultValue(DEFAULT_CONFIG)
                .name("clj-kondo config")
                .description("Provide config for clj-kondo plugin (e.g {:output {:format :edn}})")
                .build();
    }

    static List<PropertyDefinition> getProperties() {
        return asList(getEnabledProperty(), getOptionsProperty(), getConfigProperty());
    }
}