package org.sonar.plugins.clojure.sensors.cloverage;

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

public class CloverageSensorTest {


    private static final Logger LOG = Loggers.get(CloverageSensorTest.class);
    @Mock
    private CommandRunner commandRunner;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testSensorDescriptor() {
        DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
        new CloverageSensor(commandRunner).describe(descriptor);
        assertThat(descriptor.name(), is("SonarClojureCloverage"));
        assertTrue(descriptor.languages().contains("clj"));
        assertThat(descriptor.languages().size(), is(1));
    }

    @Test
    public void testExecuteSensor() throws IOException {
        SensorContextTester context = SensorContextTester.create(new File("/"));
        // Adding file to Sonar Context
        File baseDir = new File("src/test/resources/");

        File codecovResult = new File(baseDir, "cloverage-result.json");

        DefaultInputFile jsonReport = TestInputFileBuilder.create("", "target/coverage/codecov.json")
                .setLanguage(ClojureLanguage.KEY)
                .initMetadata(new String(Files.readAllBytes(codecovResult.toPath()), StandardCharsets.UTF_8))
                .setContents(new String(Files.readAllBytes(codecovResult.toPath()), StandardCharsets.UTF_8))
                .build();
        context.fileSystem().add(jsonReport);

        File fooSource = new File(baseDir, "foo.clj");
        DefaultInputFile sourceFile = TestInputFileBuilder.create("", "src/foo.clj")
                .setLanguage(ClojureLanguage.KEY)
                .initMetadata(new String(Files.readAllBytes(fooSource.toPath()), StandardCharsets.UTF_8))
                .setContents(new String(Files.readAllBytes(fooSource.toPath()), StandardCharsets.UTF_8))
                .build();
        context.fileSystem().add(sourceFile);

        CommandStreamConsumer stdOut = new CommandStreamConsumer();
        stdOut.consumeLine("Cloverage is running just fine - please relax");
        Mockito.when(commandRunner.run("lein", "cloverage", "--codecov")).thenReturn(stdOut);

        CloverageSensor cloverageSensor = new CloverageSensor(commandRunner);
        cloverageSensor.execute(context);
    }

    @Test
    public void testExecuteSensorWithMissingOutput() throws IOException {
        SensorContextTester context = SensorContextTester.create(new File("/"));


        CommandStreamConsumer stdOut = new CommandStreamConsumer();
        stdOut.consumeLine("Cloverage is running but no file could not be found");
        Mockito.when(commandRunner.run("lein", "cloverage", "--codecov")).thenReturn(stdOut);

        CloverageSensor cloverageSensor = new CloverageSensor(commandRunner);
        cloverageSensor.execute(context);
    }
}

