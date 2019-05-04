package org.sonar.plugins.clojure.sensors.cloverage;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.utils.log.LogTester;
import org.sonar.plugins.clojure.language.ClojureLanguage;
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.sonar.plugins.clojure.settings.CloverageProperties.REPORT_LOCATION_PROPERTY;

@RunWith(MockitoJUnitRunner.class)
public class CloverageSensorTest {

    private static final String MODULE_KEY = "moduleKey";

    @Mock
    private CommandRunner commandRunner;

    @Rule
    public LogTester logTester = new LogTester();

    private CloverageSensor cloverageSensor;

    @Before
    public void setUp() {
        this.cloverageSensor = new CloverageSensor(commandRunner);
    }

    @Test
    public void shouldSetSensorDescription() {
        DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
        cloverageSensor.describe(descriptor);
        assertThat(descriptor.name(), is("Cloverage"));
        assertTrue(descriptor.languages().contains("clj"));
        assertThat(descriptor.languages().size(), is(1));
    }

    @Test
    public void shouldExecuteCloverage() throws IOException {
        SensorContextTester context = SensorContextTester.create(new File("/"));
        File baseDir = new File("src/test/resources/");
        context.settings().appendProperty(REPORT_LOCATION_PROPERTY, "src/test/resources/cloverage-result.json");
        String fooPath = "src/clj/foo.clj";
        String barPath = "src/cljc/bar.cljc";
        addFileToContext(context, fooPath, baseDir, "foo_in_src_clj.clj");
        addFileToContext(context, barPath, baseDir, "bar_in_src_cljc.cljc");
        CommandStreamConsumer stdOut = new CommandStreamConsumer();
        stdOut.consumeLine("Cloverage is running just fine - please relax");
        when(commandRunner.run("lein", "cloverage", "--codecov")).thenReturn(stdOut);

        cloverageSensor.execute(context);

        String fooKey = MODULE_KEY + ":" + fooPath;
        assertThat(context.lineHits(fooKey, 1), is(1));
        assertThat(context.lineHits(fooKey, 3), is(1));
        assertThat(context.lineHits(fooKey, 5), is(0));
        assertThat(context.lineHits(fooKey, 6), is(1));

        String barKey = MODULE_KEY + ":" + barPath;
        assertThat(context.lineHits(barKey, 1), is(1));

        assertThat(logTester.logs(), hasItems("Running Cloverage"));
    }

    @Test
    public void shouldLogIfCloverageReportPathIsInvalid() {
        SensorContextTester context = SensorContextTester.create(new File("/"));
        context.settings().appendProperty(REPORT_LOCATION_PROPERTY, "invalid/file/path");
        when(commandRunner.run("lein", "cloverage", "--codecov")).thenReturn(new CommandStreamConsumer());

        cloverageSensor.execute(context);

        assertThat(logTester.logs(), hasItem("Cloverage report does not exist in the given path: invalid/file/path"));
    }

    private void addFileToContext(SensorContextTester context, String fakePath, File baseDir, String fileName) throws IOException {
        File fooSource = new File(baseDir, fileName);
        DefaultInputFile fooFile = TestInputFileBuilder.create(MODULE_KEY, fakePath)
                .setLanguage(ClojureLanguage.KEY)
                .initMetadata(new String(Files.readAllBytes(fooSource.toPath()), StandardCharsets.UTF_8))
                .setContents(new String(Files.readAllBytes(fooSource.toPath()), StandardCharsets.UTF_8))
                .build();
        context.fileSystem().add(fooFile);
    }
}

