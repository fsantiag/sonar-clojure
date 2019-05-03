package org.sonar.plugins.clojure.settings;

import org.sonar.api.config.PropertyDefinition;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;

public class Properties {

    public static final String FILE_SUFFIXES_KEY = "sonar.clojure.file.suffixes";
    public static final String FILE_SUFFIXES_DEFAULT_VALUE = "clj,cljs,cljc";
    static final String MAIN_CATEGORY = "SonarClojure";
    static final String SUB_CATEGORY = "Sensors";

    private Properties() {
    }

    public static List<PropertyDefinition> getAllProperties() {
        return Stream
                .of(getGeneralProperties(),
                    EastwoodProperties.getProperties(),
                    CloverageProperties.getProperties(),
                    AncientProperties.getProperties(),
                    KibitProperties.getProperties(),
                    NvdProperties.getProperties())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    static List<PropertyDefinition> getGeneralProperties() {
        return singletonList(getFileSuffix());
    }

    static PropertyDefinition getFileSuffix() {
        return PropertyDefinition.builder(FILE_SUFFIXES_KEY)
                .defaultValue(FILE_SUFFIXES_DEFAULT_VALUE)
                .category(MAIN_CATEGORY)
                .name("File Suffixes")
                .description("Comma-separated list of file suffixes to analyze")
                .build();
    }

}