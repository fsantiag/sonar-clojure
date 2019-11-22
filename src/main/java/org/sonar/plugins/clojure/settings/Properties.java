package org.sonar.plugins.clojure.settings;

import org.sonar.api.config.PropertyDefinition;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class Properties {

    public static final String FILE_SUFFIXES_PROPERTY = "sonar.clojure.file.suffixes";
    public static final String FILE_SUFFIXES_PROPERTY_DEFAULT = "clj,cljs,cljc";
    public static final String SENSORS_TIMEOUT_PROPERTY = "sonar.clojure.sensors.timeout";
    public static final String LEIN_PROFILE_NAME_PROPERTY = "sonar.clojure.lein.profile.name";
    public static final String SENSORS_TIMEOUT_PROPERTY_DEFAULT = "300";
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
        return asList(getFileSuffix(), getSensorsTimeout());
    }

    static PropertyDefinition getFileSuffix() {
        return PropertyDefinition.builder(FILE_SUFFIXES_PROPERTY)
                .defaultValue(FILE_SUFFIXES_PROPERTY_DEFAULT)
                .category(MAIN_CATEGORY)
                .name("File Suffixes")
                .description("Comma-separated list of file suffixes to analyze")
                .build();
    }

    static PropertyDefinition getSensorsTimeout() {
        return PropertyDefinition.builder(SENSORS_TIMEOUT_PROPERTY)
                .defaultValue(SENSORS_TIMEOUT_PROPERTY_DEFAULT)
                .category(MAIN_CATEGORY)
                .name("Sensors Timeout")
                .description("Defines the maximum timeout (per sensor, in seconds) when sensors are executing")
                .build();
    }

    static PropertyDefinition getLeinProfileName() {
        return PropertyDefinition.builder(LEIN_PROFILE_NAME_PROPERTY)
                .category(MAIN_CATEGORY)
                .name("Lein Profile Name")
                .description("Defines the name of profile can be used by lein when sensors are executing")
                .build();
    }
}