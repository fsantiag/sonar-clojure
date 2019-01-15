package org.sonar.plugins.clojure.sensors.leinNvd;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.plugins.clojure.language.ClojureSonarWayProfile;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

public class LeinNvdRulesTest {

    private BuiltInQualityProfilesDefinition.Context context;
    private ClojureSonarWayProfile clojureSonarWayProfile;

    @Before
    public void setUp() {
        context = new BuiltInQualityProfilesDefinition.Context();
        clojureSonarWayProfile = new ClojureSonarWayProfile();
        clojureSonarWayProfile.define(context);
    }


    @Test
    public void testIfSonarwayProfileIsCreatedWithAllEastwoodRules() {
        BuiltInQualityProfilesDefinition.BuiltInQualityProfile profile = context.profile("clj", "Sonar way");
        List<BuiltInQualityProfilesDefinition.BuiltInActiveRule> rules = profile.rules();
        List<String> ruleKeys = new ArrayList<>();
        ruleKeys.addAll(asList("nvd-critical",
                "nvd-high",
                "nvd-medium",
                "nvd-low"));

        ruleKeys.stream().forEach(leinNvdRule -> assertTrue(ruleKeys.contains(leinNvdRule)));
    }
}
