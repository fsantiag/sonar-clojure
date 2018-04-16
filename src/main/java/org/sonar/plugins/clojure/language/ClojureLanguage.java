package org.sonar.plugins.clojure.language;

import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;
import org.sonar.plugins.clojure.settings.ClojureProperties;

public class ClojureLanguage extends AbstractLanguage {
    public static final String KEY = "clj";
    public static final String NAME = "Clojure";

    private final Configuration config;

    public ClojureLanguage(Configuration config) {
        super(KEY, NAME);
        this.config = config;
    }

    @Override
    public String[] getFileSuffixes() {
        String[] suffixes = config.getStringArray(ClojureProperties.FILE_SUFFIXES_KEY);
        if (suffixes.length == 0) {
            suffixes = new String[]{ClojureProperties.FILE_SUFFIXES_DEFAULT_VALUE};
        }
        return suffixes;
    }

}
