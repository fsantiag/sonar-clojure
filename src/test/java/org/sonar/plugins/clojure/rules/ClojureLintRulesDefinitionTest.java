package org.sonar.plugins.clojure.rules;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;

import static org.assertj.core.api.Assertions.assertThat;

public class ClojureLintRulesDefinitionTest {

    //TODO Fiz this test. A dependency seems to be broken.
//    private ClojureLintRulesDefinition clojureLintRulesDefinition;
//    private RulesDefinition.Context context;
//
//    @Before
//    public void setUp() {
//        RulesDefinitionXmlLoader xmlLoader = Mockito.mock(RulesDefinitionXmlLoader.class);
//        Mockito.mock(RulesDefinitionXmlLoader.class);

//        clojureLintRulesDefinition = new ClojureLintRulesDefinition(xmlLoader);
//        context = new RulesDefinition.Context();
//        clojureLintRulesDefinition.define(context);
//        for (RulesDefinition.Rule rule : context.repositories().get(0).rules()) {
//            assertThat(rule.tags()).isEmpty();
//        }
//    }
//
//    @Test
//    public void testIfRepositoryOfRulesIsProperlyCreated() {
//        RulesDefinition.Repository repository = context.repository("ClojureLint");
//
//        assertThat(repository.name()).isEqualTo("ClojureLint");
//        assertThat(repository.language()).isEqualTo("clj");
//    }
}