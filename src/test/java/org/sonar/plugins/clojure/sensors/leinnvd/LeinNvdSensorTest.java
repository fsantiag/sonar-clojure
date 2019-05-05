package org.sonar.plugins.clojure.sensors.leinnvd;

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
import org.sonar.plugins.clojure.settings.NvdProperties;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class LeinNvdSensorTest {
    @Mock
    private CommandRunner commandRunner;

    private LeinNvdSensor leinNvdSensor;

    @Before
    public void setUp() {
        initMocks(this);
        leinNvdSensor = new LeinNvdSensor(commandRunner);
    }

    @Test
    public void shouldConfigureSensor() {
        DefaultSensorDescriptor descriptor = new DefaultSensorDescriptor();
        leinNvdSensor.describe(descriptor);
        assertThat(descriptor.name(), is("NVD"));
        assertTrue(descriptor.languages().contains("clj"));
        assertThat(descriptor.languages().size(), is(1));
    }

    @Test
    public void shouldExecuteLeinNvd() throws IOException {
        SensorContextTester context = prepareContext();

        CommandStreamConsumer stdOut = new CommandStreamConsumer();
        stdOut.consumeLine("We are not really interested of std out");

        leinNvdSensor.execute(context);

        List<Issue> issuesList = new ArrayList<>(context.allIssues());
        assertThat(issuesList.size(), is(2));
        assertThat(issuesList.get(0).ruleKey().rule(), is("nvd-medium"));
        assertThat(issuesList.get(0).primaryLocation().message(),
                is("CVE-2017-7656;CWE-284 Improper Access Control;jetty-util-9.2.21.v20170120.jar"));
        assertThat(issuesList.get(1).ruleKey().rule(), is("nvd-high"));
        assertThat(issuesList.get(1).primaryLocation().message(),
                is("CVE-2017-7657;CWE-190 Integer Overflow or Wraparound;jetty-util-9.2.21.v20170120.jar"));
    }

    private SensorContextTester prepareContext() throws IOException {
        SensorContextTester context = SensorContextTester.create(new File("/"));

        context.settings().appendProperty(NvdProperties.REPORT_LOCATION_PROPERTY, "src/test/resources/nvd-report.json");
        File baseDir = new File("src/test/resources/");
        File project = new File(baseDir, "project.clj");

        DefaultInputFile projectFile = TestInputFileBuilder.create("", "project.clj")
                .setLanguage(ClojureLanguage.KEY)
                .initMetadata(new String(Files.readAllBytes(project.toPath()), StandardCharsets.UTF_8))
                .setContents(new String(Files.readAllBytes(project.toPath()), StandardCharsets.UTF_8))
                .build();
        context.fileSystem().add(projectFile);

        context.setActiveRules((new ActiveRulesBuilder())
                .create(RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, "nvd-medium"))
                .activate()
                .build());

        context.setActiveRules((new ActiveRulesBuilder())
                .create(RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, "nvd-high"))
                .activate()
                .build());

        return context;
    }
}
