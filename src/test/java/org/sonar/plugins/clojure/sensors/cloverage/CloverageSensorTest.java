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
import org.sonar.plugins.clojure.language.Clojure;
import org.sonar.plugins.clojure.sensors.LeiningenRunner;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sonar.plugins.clojure.settings.CloverageProperties.REPORT_LOCATION_PROPERTY;

@RunWith(MockitoJUnitRunner.class)
public class CloverageSensorTest {

    private static final String MODULE_KEY = "moduleKey";
    public static final String FOO_PATH = "src/clj/foo.clj";
    public static final String BAR_PATH = "src/cljc/bar.cljc";

    @Mock
    private LeiningenRunner leiningenRunner;

    @Rule
    public LogTester logTester = new LogTester();

    private CloverageSensor cloverageSensor;

    @Before
    public void setUp() {
        this.cloverageSensor = new CloverageSensor(leiningenRunner);
    }

    @Test
    public void shouldConfigureSensor() {
        DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
        cloverageSensor.describe(descriptor);
        assertThat(descriptor.name(), is("Cloverage"));
        assertTrue(descriptor.languages().contains("clj"));
        assertThat(descriptor.languages().size(), is(1));
    }

    @Test
    public void shouldExecuteCloverage() throws IOException {
        SensorContextTester context = prepareContext();

        cloverageSensor.execute(context);

        String fooKey = MODULE_KEY + ":" + FOO_PATH;
        assertThat(context.lineHits(fooKey, 1), is(1));
        assertThat(context.lineHits(fooKey, 3), is(1));
        assertThat(context.lineHits(fooKey, 5), is(0));
        assertThat(context.lineHits(fooKey, 6), is(1));

        String barKey = MODULE_KEY + ":" + BAR_PATH;
        assertThat(context.lineHits(barKey, 1), is(1));

        assertThat(logTester.logs(), hasItems("Running Cloverage"));
        verify(leiningenRunner).run(300L, "cloverage", "--codecov");
    }

    @Test
    public void shouldLogIfCloverageReportPathIsInvalid() {
        SensorContextTester context = SensorContextTester.create(new File("/"));
        context.settings().appendProperty(REPORT_LOCATION_PROPERTY, "invalid/file/path");

        cloverageSensor.execute(context);

        assertThat(logTester.logs(), hasItem("Cloverage report does not exist in the given path: invalid/file/path"));
    }

    private SensorContextTester prepareContext() throws IOException {
        SensorContextTester context = SensorContextTester.create(new File("/"));
        File baseDir = new File("src/test/resources/");
        context.settings().appendProperty(REPORT_LOCATION_PROPERTY, "src/test/resources/cloverage-result.json");

        addFileToContext(context, baseDir, FOO_PATH, "foo_in_src_clj.clj");
        addFileToContext(context, baseDir, BAR_PATH, "bar_in_src_cljc.cljc");

        CommandStreamConsumer stdOut = new CommandStreamConsumer();
        stdOut.consumeLine("Cloverage is running just fine - please relax");
        when(leiningenRunner.run(300L, "cloverage", "--codecov")).thenReturn(stdOut);
        return context;
    }

    private void addFileToContext(SensorContextTester context, File baseDir, String fooPath, String s) throws IOException {
        File fooSource1 = new File(baseDir, s);
        DefaultInputFile fooFile1 = TestInputFileBuilder.create(MODULE_KEY, fooPath)
                .setLanguage(Clojure.KEY)
                .initMetadata(new String(Files.readAllBytes(fooSource1.toPath()), StandardCharsets.UTF_8))
                .setContents(new String(Files.readAllBytes(fooSource1.toPath()), StandardCharsets.UTF_8))
                .build();
        context.fileSystem().add(fooFile1);
    }

}

