package org.sonar.plugins.clojure.settings;

import java.util.List;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import static java.util.Arrays.asList;

public class ClojureProperties {

    public static final String FILE_SUFFIXES_KEY = "sonar.clojure.file.suffixes";
    public static final String FILE_SUFFIXES_DEFAULT_VALUE = ".clj";

    private ClojureProperties() {
        // only statics
    }

    public static List<PropertyDefinition> getFileSuffixProperty() {
        return asList(PropertyDefinition.builder(FILE_SUFFIXES_KEY)
                .defaultValue(FILE_SUFFIXES_DEFAULT_VALUE)
                .category("ClojureLanguage")
                .name("File Suffixes")
                .description("Comma-separated list of suffixes for files to analyze.")
                .onQualifiers(Qualifiers.PROJECT)
                .build());
    }

}