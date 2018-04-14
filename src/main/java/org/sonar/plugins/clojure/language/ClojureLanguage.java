package org.sonar.plugins.clojure.language;

import org.sonar.api.config.Configuration;
import org.sonar.api.internal.apachecommons.lang.StringUtils;
import org.sonar.api.resources.AbstractLanguage;
import org.sonar.plugins.clojure.settings.ClojureProperties;

import java.util.ArrayList;
import java.util.List;

public class ClojureLanguage extends AbstractLanguage {
    public static final String KEY = "clj";
    public static final String NAME = "Clojure";

    private final Configuration config;

    public ClojureLanguage(Configuration configuration) {
        super(KEY, NAME);
        this.config = configuration;
    }

    @Override
    public String[] getFileSuffixes() {
        String[] suffixes = filterEmptyStrings(config.getStringArray(ClojureProperties.FILE_SUFFIXES_KEY));
        if (suffixes.length == 0) {
            suffixes = ClojureProperties.FILE_SUFFIXES_DEFAULT_VALUE.split(",");
        }
        return suffixes;
    }

    private String[] filterEmptyStrings(String[] stringArray) {
        List<String> nonEmptyStrings = new ArrayList<>();
        for (String string : stringArray) {
            if (StringUtils.isNotBlank(string.trim())) {
                nonEmptyStrings.add(string.trim());
            }
        }
        return nonEmptyStrings.toArray(new String[nonEmptyStrings.size()]);
    }
}
