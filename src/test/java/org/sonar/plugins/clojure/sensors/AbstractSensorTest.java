package org.sonar.plugins.clojure.sensors;

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
import org.sonar.plugins.clojure.sensors.ancient.AncientSensor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AbstractSensorTest {
    private static final Logger LOG = Loggers.get(AbstractSensorTest.class);
    @Mock
    private CommandRunner commandRunner;

    @Before
    public void setUp() {
        initMocks(this);
    }


    @Test
    public void testIfLeinAndPluginIsinstalled(){
        List<String> osxOutputIfAncientIsNotFound =  new ArrayList<String>();
        osxOutputIfAncientIsNotFound.add("'ancient' is not a task. See 'lein help'.");
        List<String> normalOutput =  new ArrayList<String>();
        normalOutput.add("Contains something else");
        List<String> osxBashOutputILeinIsNotFound =  new ArrayList<String>();
        osxBashOutputILeinIsNotFound.add("-bash: lein: command not found");

        AbstractSensor sensor = new AncientSensor(commandRunner);
        assertFalse(sensor.isPluginInstalled(osxOutputIfAncientIsNotFound, "ancient"));
        assertFalse(sensor.isLeinInstalled(osxBashOutputILeinIsNotFound));
        assertTrue(sensor.isPluginInstalled(normalOutput, "ancient"));
        assertTrue(sensor.isLeinInstalled(normalOutput));

    }



}