package org.sonar.plugins.clojure;


import org.junit.Before;
import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;
import org.sonar.plugins.clojure.language.ClojureLanguage;
import org.sonar.plugins.clojure.language.ClojureSonarWayProfile;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;
import org.sonar.plugins.clojure.sensors.eastwood.EastwoodSensor;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class ClojurePluginTest {

    private Plugin.Context context;

    @Before
    public void setUp() {
        SonarRuntime runtime = SonarRuntimeImpl.forSonarQube(Version.create(6, 7), SonarQubeSide.SERVER);
        context = new Plugin.Context(runtime);
        new ClojurePlugin().define(context);
    }

    @Test
    public void testClojureLanguageIsAPluginExtension() {
        assertTrue(context.getExtensions().contains(ClojureLanguage.class));
    }

    @Test
    public void testClojureQualityProfileIsAPluginExtension() {
        assertTrue(context.getExtensions().contains(ClojureSonarWayProfile.class));
    }

    @Test
    public void testClojureLintRulesDefinitionIsAPluginExtension() {
        assertTrue(context.getExtensions().contains(ClojureLintRulesDefinition.class));
    }

    @Test
    public void testEastwoodSensorIsInExtensions() {
        assertTrue(context.getExtensions().contains(EastwoodSensor.class));
    }

    @Test
    public void testFileSuffixesPropertyIsInExtensions() {
        List<PropertyDefinition> propertyDefinitions = (List<PropertyDefinition>) context.getExtensions().get(8);
        PropertyDefinition suffixProperty = propertyDefinitions.get(0);
        assertThat(suffixProperty.key(), is("sonar.clojure.file.suffixes"));

    }

    @Test
    public void testExtensionsAreIncluded() {
        assertThat(context.getExtensions().size(), is(9));
    }
}