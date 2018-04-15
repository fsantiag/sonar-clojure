package org.sonar.plugins.clojure.language;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
    public void testIfSonarwayProfileIsCreatedWithAllEastwoodRules() {
        BuiltInQualityProfilesDefinition.BuiltInQualityProfile profile = context.profile("clj", "Sonar way");
        List<BuiltInQualityProfilesDefinition.BuiltInActiveRule> rules = profile.rules();
        assertThat(rules.size(), is(23));
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
        rules.stream().forEach(rule -> assertTrue(ruleKeys.contains(rule.ruleKey())));
    }

}