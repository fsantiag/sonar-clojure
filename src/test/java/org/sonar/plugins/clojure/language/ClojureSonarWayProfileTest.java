package org.sonar.plugins.clojure.language;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.plugins.clojure.language.ClojureSonarWayProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

public class ClojureSonarWayProfileTest {

    private BuiltInQualityProfilesDefinition.Context context;
    private ClojureSonarWayProfile clojureSonarWayProfile;
    private List<String> profileRules;
    @Before
    public void setUp() {
        context = new BuiltInQualityProfilesDefinition.Context();
        clojureSonarWayProfile = new ClojureSonarWayProfile();
        clojureSonarWayProfile.define(context);
        BuiltInQualityProfilesDefinition.BuiltInQualityProfile profile = context.profile("clj", "Sonar way");
        List<BuiltInQualityProfilesDefinition.BuiltInActiveRule> rules = profile.rules();
        this.profileRules = rules.stream().map(r -> r.ruleKey()).collect(Collectors.toList());

    }


    @Test
    public void testIfSonarwayProfileIsCreatedWithAllEastwoodRules() {

        List<String> ruleKeys = new ArrayList<>();
        ruleKeys.addAll(asList("bad-arglists",
                "constant-test",
                "def-in-def",
                "deprecations",
                "keyword-typos",
                "local-shadows-var",
                "misplaced-docstrings",
                "no-ns-form-found",
                "non-clojure-file",
                "redefd-vars",
                "suspicious-expression",
                "suspicious-test",
                "unlimited-use",
                "unused-fn-args",
                "unused-locals",
                "unused-meta-on-macro",
                "unused-namespaces",
                "unused-private-vars",
                "unused-ret-vals",
                "wrong-arity",
                "wrong-ns-form",
                "wrong-pre-post",
                "wrong-tag"));

        ruleKeys.stream().forEach(eastwoodRule -> assertTrue(profileRules.contains(eastwoodRule)));
    }

    @Test
    public void testIfSonarwayProfileIsCreatedWithAllAncientCljRules() {

        List<String> ruleKeys = new ArrayList<>();
        ruleKeys.addAll(asList("ancient-clj-dependency"));
        ruleKeys.stream().forEach(ancientCljRule -> assertTrue(profileRules.contains(ancientCljRule)));
    }

    @Test
    public void testIfSonarwayProfileIsCreatedWithLeinNvdRules() {

        List<String> ruleKeys = new ArrayList<>();
        ruleKeys.addAll(asList("nvd-critical",
                "nvd-high",
                "nvd-medium",
                "nvd-low"));
        ruleKeys.stream().forEach(leinNvdRule -> assertTrue(profileRules.contains(leinNvdRule)));
    }

}