package org.sonar.plugins.clojure.sensors;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommandStreamConsumerTest {

    private CommandStreamConsumer commandStreamConsumer;

    @Before
    public void setUp() {
        commandStreamConsumer = new CommandStreamConsumer();
    }

    @Test
    public void testIfDataIsSavedWhenLineIsConsumed() {
        String line = "new line of information";

        commandStreamConsumer.consumeLine(line);

        assertEquals(1, commandStreamConsumer.getData().size());
        assertEquals(line, commandStreamConsumer.getData().get(0));
    }
}