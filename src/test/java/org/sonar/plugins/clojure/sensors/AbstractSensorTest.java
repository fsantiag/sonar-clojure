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

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AbstractSensorTest {

    @Mock
    private LeiningenRunner leiningenRunner;

    @Rule
    public LogTester logTester = new LogTester();

    private DummySensor dummySensor;

    @Before
    public void setUp() {
        this.dummySensor = new DummySensor(leiningenRunner);
    }

    @Test
    public void shouldDisablePluginWhenPropertyIsSet() {
        SensorContextTester context = SensorContextTester.create(new File("/"));
        context.settings().appendProperty("property.enabled", "false");

        boolean isDisabled = dummySensor.isPluginEnabled(context, "SOME_PLUGIN", "property.enabled", true);

        assertThat(isDisabled, equalTo(false));
        assertThat(logTester.logs(), hasItem("SOME_PLUGIN disabled"));
    }

    private class DummySensor extends AbstractSensor {
        DummySensor(LeiningenRunner leiningenRunner) {
            super(leiningenRunner);
        }
    }
}