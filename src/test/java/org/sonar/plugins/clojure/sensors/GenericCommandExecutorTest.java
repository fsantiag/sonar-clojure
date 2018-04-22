package org.sonar.plugins.clojure.sensors;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GenericCommandExecutorTest {

    @Test
    public void testIfOutputOfCommandIsCorrectlyReturned() {
        CommandStreamConsumer stdout = new GenericCommandExecutor().execute("echo", 600_00, "hello");
        assertThat(stdout.getData().get(0), is("hello"));
    }

}