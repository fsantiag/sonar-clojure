package org.sonar.plugins.clojure.language;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;


public final class ClojureSonarWayProfile implements BuiltInQualityProfilesDefinition {

    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("Sonar way", ClojureLanguage.KEY);
        profile.setDefault(true);

        //TODO Read the rules from the file instead of adding one by one
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "bad-arglists");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "constant-test");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "def-in-def");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "deprecations");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "keyword-typos");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "local-shadows-var");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "misplaced-docstrings");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "no-ns-form-found");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "non-clojure-file");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "redefd-vars");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "suspicious-expression");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "suspicious-test");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "unlimited-use");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "unused-fn-args");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "unused-locals");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "unused-meta-on-macro");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "unused-namespaces");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "unused-private-vars");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "unused-ret-vals");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "wrong-arity");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "wrong-ns-form");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "wrong-pre-post");
        profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, "wrong-tag");

        profile.done();
    }

}