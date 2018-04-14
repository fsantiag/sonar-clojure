package org.sonar.plugins.clojure.language;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;


public final class ClojureQualityProfile implements BuiltInQualityProfilesDefinition {

    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("ClojureLint Rules", ClojureLanguage.KEY);
        profile.setDefault(true);

        NewBuiltInActiveRule rule1 = profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "unused-ret-vals");
        rule1.overrideSeverity("BLOCKER");
        NewBuiltInActiveRule rule2 = profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "wrong-arity");
        rule2.overrideSeverity("MAJOR");
        NewBuiltInActiveRule rule3 = profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "ExampleRule3");
        rule3.overrideSeverity("CRITICAL");

        profile.done();
    }

}