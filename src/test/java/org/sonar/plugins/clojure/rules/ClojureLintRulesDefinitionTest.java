package org.sonar.plugins.clojure.rules;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClojureLintRulesDefinitionTest {

    private ClojureLintRulesDefinition clojureLintRulesDefinition;
    private RulesDefinition.Context context;

    @Before
    public void setUp() {
        RulesDefinitionXmlLoader xmlLoader = new RulesDefinitionXmlLoader();
        clojureLintRulesDefinition = new ClojureLintRulesDefinition(xmlLoader);
        context = new RulesDefinition.Context();
        clojureLintRulesDefinition.define(context);
    }

    @Test
    public void testIfRepositoryOfRulesIsProperlyCreated() {
        RulesDefinition.Repository repository = context.repository("ClojureLint");

        assertThat(repository.name(), is("ClojureLint"));
        assertThat(repository.language(), is("clj"));
    }
}