package org.sonar.plugins.clojure.sensors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.log.Logger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommandRunnerTest {

    @Mock
    private Logger logger;

    @Mock
    private CommandExecutor commandExecutor;

    private CommandRunner commandRunner;

    @Before
    public void setUp() {
        commandRunner = new CommandRunner(logger, commandExecutor);
    }

    @Test
    public void shouldTakeMultipleArgumentsForCommand() {
        commandRunner.run(300L, "echo", "argument1", null, "argument2");

        ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(commandExecutor, times(1))
                .execute(
                        commandCaptor.capture(),
                        any(CommandStreamConsumer.class),
                        any(CommandStreamConsumer.class),
                        anyLong());

        Command cmd = commandCaptor.getValue();
        assertThat(cmd.getArguments().size(), is(2));
        assertThat(cmd.getArguments().get(0), is("argument1"));
        assertThat(cmd.getArguments().get(1), is("argument2"));
    }

    @Test
    public void shouldLogStdoutStderrAndCommandInDebugMode() {
        CommandStreamConsumer stdout = new CommandStreamConsumer();
        stdout.consumeLine("line in stdout");
        CommandStreamConsumer stderr = new CommandStreamConsumer();
        stderr.consumeLine("line in stderr");

        commandRunner.run("echo", stdout, stderr, 300L, "argument1", "argument2");

        verify(logger, times(1)).debug("command: [argument1, argument2]");
        verify(logger, times(1)).debug("stdout: line in stdout");
        verify(logger, times(1)).debug("stderr: line in stderr");
    }

    @Test
    public void shouldLogErrorForReturnCodeDifferentThanZero() {
        when(commandExecutor.execute(any(),any(),any(),anyLong())).thenReturn(1);
        CommandStreamConsumer dummyStreamConsumer = new CommandStreamConsumer();

        commandRunner.run("echo", dummyStreamConsumer, dummyStreamConsumer, 300L, "argument1", "argument2");
        verify(logger, times(1)).warn("Command: [argument1, argument2] returned a non-zero " +
                "code. Please make sure plugin is working isolated before running sonar-scanner");
    }

}