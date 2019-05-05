package org.sonar.plugins.clojure.language;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.plugins.clojure.settings.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClojureLanguageTest {

    private MapSettings settings;
    private ClojureLanguage language;

    @Before
    public void setUp() {
        settings = new MapSettings();
        language = new ClojureLanguage(settings.asConfig());
    }

    @Test
    public void testClojureLanguageFileSuffixesWhenKeyIsDefault() {
        assertThat(language.getFileSuffixes(), is(new String[]{"clj","cljs","cljc"}));
    }

    @Test
    public void testClojureLanguageFileSuffixesWhenEmptyKeyIsSet() {
        settings.setProperty(Properties.FILE_SUFFIXES_PROPERTY, "");
        assertThat(language.getFileSuffixes(), is(new String[]{"clj","cljs","cljc"}));
    }

    @Test
    public void testClojureLanguageFileSuffixesWhenKeyIsCustom() {
        settings.setProperty(Properties.FILE_SUFFIXES_PROPERTY, ".foo");
        assertThat(language.getFileSuffixes(), is(new String[]{".foo"}));
    }

    @Test
    public void testClojureLanguageFileSuffixesWhenKeyIsMultiple() {
        settings.setProperty(Properties.FILE_SUFFIXES_PROPERTY, ".foo,.bar,.baz");
        assertThat(language.getFileSuffixes(), is(new String[]{".foo", ".bar", ".baz"}));
    }

}