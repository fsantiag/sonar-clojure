package org.sonar.plugins.clojure.language;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClojureSonarWayProfileTest {

    private BuiltInQualityProfilesDefinition.Context context;
    private ClojureSonarWayProfile clojureSonarWayProfile;

    @Before
    public void setUp() {
        context = new BuiltInQualityProfilesDefinition.Context();
        clojureSonarWayProfile = new ClojureSonarWayProfile();
        clojureSonarWayProfile.define(context);
    }

    @Test
    public void testIfProfileIsCreatedWithOnlyOneRule() {
        BuiltInQualityProfilesDefinition.BuiltInQualityProfile profile = context.profile("clj", "Sonar way");
        List<BuiltInQualityProfilesDefinition.BuiltInActiveRule> rules = profile.rules();
        assertThat(rules.size(), is(1));
        assertThat(rules.get(0).ruleKey(), is("unused-ret-vals"));
    }

}