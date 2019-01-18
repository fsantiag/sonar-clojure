package org.sonar.plugins.clojure.settings;

import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import java.util.List;

import static java.util.Arrays.asList;

public class ClojureProperties {

    public static final String FILE_SUFFIXES_KEY = "sonar.clojure.file.suffixes";
    public static final String FILE_SUFFIXES_DEFAULT_VALUE = "clj,cljs,cljc";

    private ClojureProperties() {}

    public static List<PropertyDefinition> getProperties() {
        return asList(getFileSuffixProperty(),
                getEastwoodDisabledProperty(),
                getAncientCljDisabledProperty());
    }

    public static PropertyDefinition getFileSuffixProperty() {
        return PropertyDefinition.builder(FILE_SUFFIXES_KEY)
                .defaultValue(FILE_SUFFIXES_DEFAULT_VALUE)
                .category("ClojureLanguage")
                .name("File Suffixes")
                .description("Comma-separated list of suffixes for files to analyze.")
                .build();
    }

    public static PropertyDefinition getEastwoodDisabledProperty() {
        return PropertyDefinition.builder("sonar.clojure.eastwood.disabled")
                .category("ClojureLanguage")
                .subCategory("Sensors")
                .defaultValue("false")
                .name("Eastwood sensor disabling")
                .description("Set true to disable Eastwood sensor.")
                .build();
    }

    public static PropertyDefinition getAncientCljDisabledProperty() {
        return PropertyDefinition.builder("sonar.clojure.ancient-clj.disabled")
                .category("ClojureLanguage")
                .subCategory("Sensors")
                .defaultValue("false")
                .name("ancient-clj sensor disabling")
                .description("Set true to disable ancient-clj sensor.")
                .build();
    }
}