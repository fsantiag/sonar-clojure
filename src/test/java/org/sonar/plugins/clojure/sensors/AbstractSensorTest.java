package org.sonar.plugins.clojure.sensors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.sonar.plugins.clojure.sensors.ancient.AncientSensor;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AbstractSensorTest {

    @Mock
    private CommandRunner commandRunner;

    @Before
    public void setUp() {
        initMocks(this);
    }


    @Test
    public void testIfLeinIsInstalled(){
        List<String> normalOutput =  new ArrayList<String>();
        normalOutput.add("Contains something else");
        List<String> osxBashOutputILeinIsNotFound =  new ArrayList<String>();
        osxBashOutputILeinIsNotFound.add("-bash: lein: command not found");

        AbstractSensor sensor = new AncientSensor(commandRunner);
        assertFalse(sensor.isLeinInstalled(osxBashOutputILeinIsNotFound));
        assertTrue(sensor.isLeinInstalled(normalOutput));

    }

    @Test
    public void testIfPluginIsInstalled(){
        List<String> osxOutputIfAncientIsNotFound =  new ArrayList<String>();
        osxOutputIfAncientIsNotFound.add("'ancient' is not a task. See 'lein help'.");
        List<String> normalOutput =  new ArrayList<String>();
        normalOutput.add("Contains something else");

        AbstractSensor sensor = new AncientSensor(commandRunner);
        assertFalse(sensor.isPluginInstalled(osxOutputIfAncientIsNotFound, "ancient"));
        assertTrue(sensor.isPluginInstalled(normalOutput, "ancient"));
    }



}