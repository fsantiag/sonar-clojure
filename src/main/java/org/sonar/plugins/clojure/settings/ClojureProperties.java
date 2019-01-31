package org.sonar.plugins.clojure.settings;

import org.sonar.api.config.PropertyDefinition;

import java.util.List;

import static java.util.Arrays.asList;

public class ClojureProperties {

    public static final String FILE_SUFFIXES_KEY = "sonar.clojure.file.suffixes";
    public static final String FILE_SUFFIXES_DEFAULT_VALUE = "clj,cljs,cljc";
    public static final String ANCIENT_CLJ_DISABLED = "sonar.clojure.ancient-clj.disabled";
    public static final String EASTWOOD_DISABLED = "sonar.clojure.eastwood.disabled";
    public static final String LEIN_NVD_DISABLED = "sonar.clojure.lein-nvd.disabled";
    public static final String LEIN_NVD_JSON_OUTPUT_LOCATION = "sonar.clojure.lein-nvd.json-output-location";
    public static final String MAIN_CATEGORY = "ClojureLanguage";

    private ClojureProperties() {}

    public static List<PropertyDefinition> getProperties() {
        return asList(getFileSuffixProperty(),
                getEastwoodDisabledProperty(),
                getAncientCljDisabledProperty(),
                getLeinNvdDisabledProperty(),
                getLeinNVdXMLOutputLocation());
    }

    public static PropertyDefinition getFileSuffixProperty() {
        return PropertyDefinition.builder(FILE_SUFFIXES_KEY)
                .defaultValue(FILE_SUFFIXES_DEFAULT_VALUE)
                .category(MAIN_CATEGORY)
                .name("File Suffixes")
                .description("Comma-separated list of suffixes for files to analyze.")
                .build();
    }

    public static PropertyDefinition getEastwoodDisabledProperty() {
        return PropertyDefinition.builder(EASTWOOD_DISABLED)
                .category(MAIN_CATEGORY)
                .subCategory("Sensors")
                .defaultValue("false")
                .name("Eastwood sensor disabling")
                .description("Set true to disable Eastwood sensor.")
                .build();
    }

    public static PropertyDefinition getAncientCljDisabledProperty() {
        return PropertyDefinition.builder(ANCIENT_CLJ_DISABLED)
                .category(MAIN_CATEGORY)
                .subCategory("Sensors")
                .defaultValue("false")
                .name("ancient-clj sensor disabling")
                .description("Set true to disable ancient-clj sensor.")
                .build();
    }

    public static PropertyDefinition getLeinNvdDisabledProperty() {
        return PropertyDefinition.builder(LEIN_NVD_DISABLED)
                .category("ClojureLanguage")
                .subCategory("Sensors")
                .defaultValue("false")
                .name("Lein NVD sensor disabling")
                .description("Set true to disable Lein NVD sensor.")
                .build();
    }

    public static PropertyDefinition getLeinNVdXMLOutputLocation() {
        return PropertyDefinition.builder(LEIN_NVD_JSON_OUTPUT_LOCATION)
                .category("ClojureLanguage")
                .subCategory("Sensors")
                .defaultValue("src/test/resources/nvd-report.json")
                .name("Lein NVD output location")
                .description("Set this to path where Lein NVD generates the result xml file.")
                .build();
    }
}