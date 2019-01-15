package org.sonar.plugins.clojure.sensors.kibit;

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
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
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

public class KibitSensorTest {

    @Mock
    private CommandRunner commandRunner;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testSensorDescriptor() {
        DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
        new KibitSensor(commandRunner).describe(descriptor);
        assertThat(descriptor.name(), is("SonarClojureKibit"));
        assertTrue(descriptor.languages().contains("clj"));
        assertThat(descriptor.languages().size(), is(1));
    }

    @Test
    public void testExecuteSensor() throws IOException {
        SensorContextTester context = SensorContextTester.create(new File("src/test/resources/"));

        File baseDir = new File("src/test/resources/");
        File file = new File(baseDir, "kibit.clj");
        DefaultInputFile inputFile = TestInputFileBuilder.create("", "kibit.clj")
                .setLanguage(ClojureLanguage.KEY)
                .initMetadata(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8))
                .build();

        context.fileSystem().add(inputFile);

        context.setActiveRules((new ActiveRulesBuilder())
                .create(RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, "kibit"))
                .activate()
                .build());

        CommandStreamConsumer stdOut = new CommandStreamConsumer();
        stdOut.consumeLine("----");
        stdOut.consumeLine("##### `kibit.clj:5`");
        stdOut.consumeLine("Kibit will say that there is pos? function available");
        Mockito.when(commandRunner.run("lein", "kibit", "-r", "markdown")).thenReturn(stdOut);

        KibitSensor kibitSensor = new KibitSensor(commandRunner);
        kibitSensor.execute(context);

        List<Issue> issuesList = context.allIssues().stream().collect(Collectors.toList());
        assertThat(issuesList.size(), is(1));
        assertThat(issuesList.get(0).ruleKey().rule(), is("kibit"));
    }
}