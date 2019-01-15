package org.sonar.plugins.clojure;

import org.junit.Test;
import org.sonar.plugins.clojure.sensors.eastwood.EastwoodSensor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertTrue;

public class SonarProjectPropertiesTest {

    @Test
    public void testInitialize() throws FileNotFoundException {
        SonarProjectProperties props = new SonarProjectProperties();
        props.initialize(new FileInputStream("src/test/resources/sonar-project.properties"));
        assertTrue(props.isSensorDisabled(EastwoodSensor.class));
        assertTrue(!props.isSensorDisabled(FileNotFoundException.class));

    }

}
