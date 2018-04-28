package org.sonar.plugins.clojure.sensors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.command.StreamConsumer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommandRunnerTest {

    @Test
    public void testMultipleArgumentsCommand() {
        CommandStreamConsumer stdOut = new CommandRunner().run("echo", "argument1", "argument2");
        assertThat(stdOut.getData().get(0), is("argument1 argument2"));
        assertThat(stdOut.getData().size(), is(1));
    }

}