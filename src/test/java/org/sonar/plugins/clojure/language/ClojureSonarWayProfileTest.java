package org.sonar.plugins.clojure.language;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertTrue;
import static org.sonar.api.server.profile.BuiltInQualityProfilesDefinition.*;

public class ClojureSonarWayProfileTest {

    private List<String> profileRules;

    @Before
    public void setUp() {
        Context context = new Context();
        ClojureSonarWayProfile clojureSonarWayProfile = new ClojureSonarWayProfile();
        clojureSonarWayProfile.define(context);
        BuiltInQualityProfile profile = context.profile("clj", "Sonar way");
        List<BuiltInActiveRule> rules = profile.rules();
        this.profileRules = rules.stream().map(BuiltInActiveRule::ruleKey).collect(Collectors.toList());

    }

    @Test
    public void testIfSonarwayProfileIsCreatedWithAllEastwoodRule() {
        List<String> ruleKeys = new ArrayList<>(singletonList("eastwood"));
        ruleKeys.forEach(eastwoodRule -> assertTrue(profileRules.contains(eastwoodRule)));
    }

    @Test
    public void testIfSonarwayProfileIsCreatedWithAllAncientCljRule() {
        List<String> ruleKeys = new ArrayList<>(singletonList("ancient-clj-dependency"));
        ruleKeys.forEach(ancientCljRule -> assertTrue(profileRules.contains(ancientCljRule)));
    }

    @Test
    public void testIfSonarwayProfileIsCreatedWithLeinNvdRules() {
        List<String> ruleKeys = new ArrayList<>(asList("nvd-critical", "nvd-high", "nvd-medium", "nvd-low"));
        ruleKeys.forEach(leinNvdRule -> assertTrue(profileRules.contains(leinNvdRule)));
    }

    @Test
    public void testIfSonarwayProfileIsCreatedWithKibitRule() {
        List<String> ruleKeys = new ArrayList<>(singletonList("kibit"));
        ruleKeys.forEach(kibitRule -> assertTrue(profileRules.contains(kibitRule)));
    }
}