package org.sonar.plugins.clojure.sensors.kondo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.DefaultTextPointer;
import org.sonar.api.batch.fs.internal.DefaultTextRange;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.sonar.plugins.clojure.settings.KondoProperties.*;

@RunWith(MockitoJUnitRunner.class)
public class KondoSensorTest {

    @Mock
    private LeiningenRunner commandRunner;

    private KondoSensor kondoSensor;

    @Before
    public void setUp() {
        kondoSensor = new KondoSensor(commandRunner);
    }

    @Test
    public void shouldConfigureSensor() {
        DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
        kondoSensor.describe(descriptor);
        assertThat(descriptor.name(), is("clj-kondo"));
        assertThat(descriptor.languages().contains("clj"), is(true));
        assertThat(descriptor.languages().size(), is(1));
    }

    @Test
    public void shouldExecuteKondo() throws IOException {
        SensorContextTester context = prepareContext();

        CommandStreamConsumer stdOut = new CommandStreamConsumer();
        stdOut.consumeLine("{:findings [{:type :unused-binding, :filename \"file.clj\", " +
                ":message \"unused binding args\", :row 1, :col 16, :end-row 1, :end-col 20, :level :warning}], " +
                ":summary {:error 0, :warning 1, :info 0, :type :summary, :duration 11, :files 1}}\n");
        when(commandRunner.run(300L, "run", "-m", "clj-kondo.main", "--lint", "src", "--config", "{:output {:format :edn}}"))
                .thenReturn(stdOut);

        kondoSensor.execute(context);

        TextRange expectedRange = new DefaultTextRange(new DefaultTextPointer(1, 15), new DefaultTextPointer(1, 19));

        List<Issue> issuesList = new ArrayList<>(context.allIssues());
        assertThat(issuesList.size(), is(1));
        assertThat(issuesList.get(0).ruleKey().rule(), is("kondo"));
        assertThat(issuesList.get(0).primaryLocation().message(), is("unused binding args"));
        assertThat(issuesList.get(0).primaryLocation().textRange(), is(expectedRange));
    }

    private SensorContextTester prepareContext() throws IOException {
        SensorContextTester context = SensorContextTester.create(new File("src/test/resources/"));

        context.settings().appendProperty(OPTIONS, "--lint src");
        context.settings().appendProperty(CONFIG, "{:output {:format :edn}}");
        context.settings().appendProperty(ENABLED_PROPERTY, "true");

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