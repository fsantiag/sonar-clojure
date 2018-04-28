package org.sonar.plugins.clojure.sensors;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CommandRunnerTest {

    @Test
    public void testMultipleArgumentsCommand() {
        CommandStreamConsumer stdOut = new CommandRunner().run("echo", "argument1", "argument2");
        assertThat(stdOut.getData().get(0), is("argument1 argument2"));
        assertThat(stdOut.getData().size(), is(1));
    }

}