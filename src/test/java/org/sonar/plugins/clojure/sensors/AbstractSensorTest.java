package org.sonar.plugins.clojure.sensors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.utils.log.LogTester;

import java.io.File;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class AbstractSensorTest {

    @Mock
    private CommandRunner commandRunner;

    @Rule
    public LogTester logTester = new LogTester();

    private DummySensor dummySensor;

    @Before
    public void setUp() {
        this.dummySensor = new DummySensor(commandRunner);
    }

    @Test
    public void shouldDisablePluginWhenPropertyIsSet() {
        SensorContextTester context = SensorContextTester.create(new File("/"));
        context.settings().appendProperty("property.foo", "true");

        boolean isDisabled = dummySensor.isPluginDisabled(context, "SOME_PLUGIN", "property.foo", false);

        assertTrue(isDisabled);
        assertThat(logTester.logs(), hasItem("SOME_PLUGIN disabled"));
    }

    private class DummySensor extends AbstractSensor {
        DummySensor(CommandRunner commandRunner) {
            super(commandRunner);
        }
    }
}