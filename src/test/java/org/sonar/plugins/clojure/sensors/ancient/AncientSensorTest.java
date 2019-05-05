package org.sonar.plugins.clojure.sensors.ancient;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.rule.RuleKey;
import org.sonar.plugins.clojure.language.ClojureLanguage;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AncientSensorTest {
    @Mock
    private CommandRunner commandRunner;

    private AncientSensor ancientSensor;

    @Before
    public void setUp() {
        initMocks(this);
        ancientSensor = new AncientSensor(commandRunner);
    }

    @Test
    public void shouldConfigureSensor() {
        DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
        ancientSensor.describe(descriptor);
        assertThat(descriptor.name(), is("Ancient"));
        assertTrue(descriptor.languages().contains("clj"));
        assertThat(descriptor.languages().size(), is(1));
    }

    @Test
    public void shouldExecuteAncient() throws IOException {
        SensorContextTester context = prepareContext();

        CommandStreamConsumer stdOut = new CommandStreamConsumer();
        stdOut.consumeLine("This is some non related line which should not end to report");
        stdOut.consumeLine("[metosin/reitit \"0.2.10\"] is available but we use \"0.2.1\"");
        stdOut.consumeLine("[metosin/ring-http-response \"0.9.1\"] is available but we use \"0.9.0\"");
        when(commandRunner.run(any(), eq("lein"), eq("ancient"))).thenReturn(stdOut);

        ancientSensor.execute(context);

        List<Issue> issuesList = new ArrayList<>(context.allIssues());

        assertThat(issuesList.size(), is(2));
        assertThat(issuesList.get(0).ruleKey().rule(), is("ancient-clj-dependency"));
        assertThat(issuesList.get(0).primaryLocation().message(),
                is("metosin/reitit is using version: 0.2.1 but version: 0.2.10 is available."));

        assertThat(issuesList.get(1).ruleKey().rule(), is("ancient-clj-dependency"));
        assertThat(issuesList.get(1).primaryLocation().message(),
                is("metosin/ring-http-response is using version: 0.9.0 but version: 0.9.1 is available."));
    }

    private SensorContextTester prepareContext() throws IOException {
        SensorContextTester context = SensorContextTester.create(new File("/"));

        File baseDir = new File("src/test/resources/");
        File file = new File(baseDir, "project.clj");

        DefaultInputFile inputFile = TestInputFileBuilder.create("moduleKey", "project.clj")
                .setLanguage(ClojureLanguage.KEY)
                .initMetadata(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8))
                .setContents(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8))
                .build();
        context.fileSystem().add(inputFile);

        context.setActiveRules((new ActiveRulesBuilder())
                .create(RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, "ancient-dependency"))
                .activate()
                .build());
        return context;
    }
}