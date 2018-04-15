package org.sonar.plugins.clojure.language;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;


public final class ClojureSonarWayProfile implements BuiltInQualityProfilesDefinition {

    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("Sonar way", ClojureLanguage.KEY);
        profile.setDefault(true);

        NewBuiltInActiveRule rule1 = profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "unused-ret-vals");
        rule1.overrideSeverity("BLOCKER");

        profile.done();
    }

}