package org.sonar.plugins.clojure.sensors.kibit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.plugins.clojure.language.Clojure;
import org.sonar.plugins.clojure.sensors.LeiningenRunner;
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
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class KibitSensorTest {

    @Mock
    private LeiningenRunner leiningenRunner;

    private KibitSensor kibitSensor;

    @Before
    public void setUp() {
        initMocks(this);
        kibitSensor = new KibitSensor(leiningenRunner);
    }

    @Test
    public void shouldConfigureSensor() {
        DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
        kibitSensor.describe(descriptor);
        assertThat(descriptor.name(), is("Kibit"));
        assertTrue(descriptor.languages().contains("clj"));
        assertThat(descriptor.languages().size(), is(1));
    }

    @Test
    public void shouldExecuteKibit() throws IOException {
        SensorContextTester context = prepareContext();

        CommandStreamConsumer stdOut = new CommandStreamConsumer();
        stdOut.consumeLine("----");
        stdOut.consumeLine("At kibit.clj:5:");
        stdOut.consumeLine("Kibit will say that there is pos? function available");
        when(leiningenRunner.run(300L, "kibit")).thenReturn(stdOut);

        kibitSensor.execute(context);

        List<Issue> issuesList = new ArrayList<>(context.allIssues());
        assertThat(issuesList.size(), is(1));
        assertThat(issuesList.get(0).ruleKey().rule(), is("kibit"));
    }

    private SensorContextTester prepareContext() throws IOException {
        SensorContextTester context = SensorContextTester.create(new File("src/test/resources/"));

        File baseDir = new File("src/test/resources/");
        File file = new File(baseDir, "kibit.clj");
        DefaultInputFile inputFile = TestInputFileBuilder.create("", "kibit.clj")
                .setLanguage(Clojure.KEY)
                .initMetadata(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8))
                .build();

        context.fileSystem().add(inputFile);

        return context;
    }
}