package org.sonar.plugins.clojure.rules;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.plugins.clojure.language.ClojureLanguage;

import java.nio.charset.StandardCharsets;

public final class ClojureLintRulesDefinition implements RulesDefinition {

    private static final String RULES_PATH = "/clojure/rules.xml";
    public static final String REPOSITORY_NAME = "ClojureLint";
    public static final String REPOSITORY_KEY = REPOSITORY_NAME;

    private final RulesDefinitionXmlLoader xmlLoader;

    public ClojureLintRulesDefinition(RulesDefinitionXmlLoader xmlLoader) {
        this.xmlLoader = xmlLoader;
    }

    @Override
    public void define(Context context) {
        NewRepository repository = context.createRepository(REPOSITORY_KEY, ClojureLanguage.KEY).setName(REPOSITORY_NAME);
        xmlLoader.load(repository, getClass().getResourceAsStream(RULES_PATH), StandardCharsets.UTF_8.name());
        repository.done();
    }
}