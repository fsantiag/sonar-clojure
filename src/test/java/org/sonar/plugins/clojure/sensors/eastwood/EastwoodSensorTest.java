package org.sonar.plugins.clojure.sensors.eastwood;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.rule.RuleKey;
import org.sonar.plugins.clojure.language.ClojureLanguage;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.sensors.GenericCommandExecutor;
import org.sonar.plugins.clojure.sensors.eastwood.EastwoodSensor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class EastwoodSensorTest {

    @Mock
    private GenericCommandExecutor eastwoodExecutor;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testSensorDescriptor() {
        DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
        new EastwoodSensor(eastwoodExecutor).describe(descriptor);
        assertThat(descriptor.name(), is("SonarClojure"));
        assertTrue(descriptor.languages().contains("clj"));
        assertThat(descriptor.languages().size(), is(1));
        assertThat(descriptor.name(), is("SonarClojure"));
        assertThat(descriptor.name(), is("SonarClojure"));
    }

    @Test
    public void testExecuteSensor() throws IOException {
        SensorContextTester context = SensorContextTester.create(new File("src/test/resources/"));

        File baseDir = new File("src/test/resources/");
        File file = new File(baseDir, "file.clj");
        DefaultInputFile inputFile = TestInputFileBuilder.create("", "file.clj")
                .setLanguage(ClojureLanguage.KEY)
                .initMetadata(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8))
                .build();
        context.fileSystem().add(inputFile);

        context.setActiveRules((new ActiveRulesBuilder())
                .create(RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, "issue-1"))
                .setName("Invalid name")
                .activate()
                .create(RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, "issue-2"))
                .setName("Missing docstring")
                .activate()
                .build());

        CommandStreamConsumer stdOut = new CommandStreamConsumer();
        stdOut.consumeLine("file.clj:1:0:issue-1:description-1");
        stdOut.consumeLine("file.clj:2:0:issue-2:description-2");
        Mockito.when(eastwoodExecutor.execute("lein", 600_00, "eastwood")).thenReturn(stdOut);

        EastwoodSensor eastwoodSensor = new EastwoodSensor(eastwoodExecutor);
        eastwoodSensor.execute(context);

        assertThat(context.allIssues().size(), is(2));
        List<Issue> issuesList = context.allIssues().stream().collect(Collectors.toList());
        assertThat(issuesList.get(0).ruleKey().rule(), is("issue-1"));
        assertThat(issuesList.get(1).ruleKey().rule(), is("issue-2"));
    }
}