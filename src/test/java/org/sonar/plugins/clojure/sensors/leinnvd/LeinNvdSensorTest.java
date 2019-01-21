package org.sonar.plugins.clojure.sensors.leinnvd;

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
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.language.ClojureLanguage;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.settings.ClojureProperties;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class LeinNvdSensorTest {

    private static final Logger LOG = Loggers.get(LeinNvdSensorTest.class);
    @Mock
    private CommandRunner commandRunner;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testSensorDescriptor() {
        DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
        new LeinNvdSensor(commandRunner).describe(descriptor);
        assertThat(descriptor.name(), is("SonarClojureLeinNvd"));
        assertTrue(descriptor.languages().contains("clj"));
        assertThat(descriptor.languages().size(), is(1));
    }

    @Test
    public void testExecuteSensor() throws IOException {
        SensorContextTester context = SensorContextTester.create(new File("/"));

        context.settings().appendProperty(ClojureProperties.LEIN_NVD_JSON_OUTPUT_LOCATION, "src/test/resources/nvd-report.json");
        // Adding file to Sonar Context
        File baseDir = new File("src/test/resources/");
        File project = new File(baseDir, "project.clj");

        DefaultInputFile projectFile = TestInputFileBuilder.create("", "project.clj")
                .setLanguage(ClojureLanguage.KEY)
                .initMetadata(new String(Files.readAllBytes(project.toPath()), StandardCharsets.UTF_8))
                .setContents(new String(Files.readAllBytes(project.toPath()), StandardCharsets.UTF_8))
                .build();
        context.fileSystem().add(projectFile);

        // Creating fake rules to the Sonar Context
        context.setActiveRules((new ActiveRulesBuilder())
                .create(RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, "nvd-medium"))
                .activate()
                .build());

        context.setActiveRules((new ActiveRulesBuilder())
                .create(RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, "nvd-high"))
                .activate()
                .build());

        CommandStreamConsumer stdOut = new CommandStreamConsumer();
        stdOut.consumeLine("We are not really interested of std out");
        Mockito.when(commandRunner.run("lein", "nvd", "check")).thenReturn(stdOut);

        LeinNvdSensor leinNvdSensor = new LeinNvdSensor(commandRunner);
        leinNvdSensor.execute(context);

        List<Issue> issuesList = context.allIssues().stream().collect(Collectors.toList());
        assertThat(issuesList.size(), is(2));
        assertThat(issuesList.get(0).ruleKey().rule(), is("nvd-medium"));
        assertThat(issuesList.get(1).ruleKey().rule(), is("nvd-high"));
    }

    @Test
    public void testExecuteSensorWithNonExistingProject() throws IOException {
        SensorContextTester context = SensorContextTester.create(new File("/"));

        CommandStreamConsumer stdOut = new CommandStreamConsumer();
        stdOut.consumeLine("This is some non related line which should not end to report");
        stdOut.consumeLine("[metosin/reitit \"0.2.10\"] is available but we use \"0.2.1\"");
        stdOut.consumeLine("[metosin/ring-http-response \"0.9.1\"] is available but we use \"0.9.0\"");
        Mockito.when(commandRunner.run("lein", "nvd", "check")).thenReturn(stdOut);
        context.settings().appendProperty(ClojureProperties.LEIN_NVD_JSON_OUTPUT_LOCATION, "src/test/resources/nvd-report.json");
        LeinNvdSensor leinNvdSensor = new LeinNvdSensor(commandRunner);
        leinNvdSensor.execute(context);
    }
}
