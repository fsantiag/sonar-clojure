package org.sonar.plugins.clojure.sensors.eastwood;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.plugins.clojure.language.Clojure;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.sensors.LeiningenRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.sonar.plugins.clojure.sensors.eastwood.EastwoodProperties.EASTWOOD_OPTIONS;

@RunWith(MockitoJUnitRunner.class)
public class EastwoodSensorTest {

    @Mock
    private LeiningenRunner leiningenRunner;

    private EastwoodSensor eastwoodSensor;

    @Before
    public void setUp() {
        eastwoodSensor = new EastwoodSensor(leiningenRunner);
    }

    @Test
    public void shouldConfigureSensor() {
        DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
        eastwoodSensor.describe(descriptor);
        assertThat(descriptor.name(), is("Eastwood"));
        assertTrue(descriptor.languages().contains("clj"));
        assertThat(descriptor.languages().size(), is(1));
    }

    @Test
    public void shouldExecuteEastwood() throws IOException {
        SensorContextTester context = prepareContext();

        CommandStreamConsumer stdOut = new CommandStreamConsumer();
        stdOut.consumeLine("file.clj:1:0:issue-1:description-1");
        stdOut.consumeLine("file.clj:2:0:issue-2:description-2");
        String options = "eastwood-option";
        when(leiningenRunner.run(300L, "eastwood", options)).thenReturn(stdOut);

        eastwoodSensor.execute(context);

        List<Issue> issuesList = new ArrayList<>(context.allIssues());
        assertThat(issuesList.size(), is(2));
        assertThat(issuesList.get(0).ruleKey().rule(), is("eastwood"));
        assertThat(issuesList.get(0).primaryLocation().message(), is("description-1"));
        assertThat(issuesList.get(1).ruleKey().rule(), is("eastwood"));
        assertThat(issuesList.get(1).primaryLocation().message(), is("description-2"));
    }

    private SensorContextTester prepareContext() throws IOException {
        SensorContextTester context = SensorContextTester.create(new File("src/test/resources/"));

        context.settings().appendProperty(EASTWOOD_OPTIONS, "eastwood-option");

        File baseDir = new File("src/test/resources/");
        File file = new File(baseDir, "file.clj");
        DefaultInputFile inputFile = TestInputFileBuilder.create("", "file.clj")
                .setLanguage(Clojure.KEY)
                .initMetadata(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8))
                .build();
        context.fileSystem().add(inputFile);

        return context;
    }
}