package org.sonar.plugins.clojure.language;

import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;
import org.sonar.plugins.clojure.settings.Properties;

public class Clojure extends AbstractLanguage {
    public static final String KEY = "clj";
    public static final String NAME = "Clojure";

    private final Configuration config;

    public Clojure(Configuration config) {
        super(KEY, NAME);
        this.config = config;
    }

    @Override
    public String[] getFileSuffixes() {
        String[] suffixes = config.getStringArray(Properties.FILE_SUFFIXES_PROPERTY);
        if (suffixes.length == 0) {
            suffixes = Properties.FILE_SUFFIXES_PROPERTY_DEFAULT.split(",");
        }
        return suffixes;
    }

}
