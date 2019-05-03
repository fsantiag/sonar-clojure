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
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        commandRunner.run("echo", "argument1", null, "argument2");

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
    public void shouldLogCommandOutputWhenDebugIsEnabled() {
        when(logger.isDebugEnabled()).thenReturn(true);
        CommandStreamConsumer stdout = new CommandStreamConsumer();
        String commandOutput = "command output";
        stdout.consumeLine(commandOutput);

        commandRunner.run("echo", stdout, new CommandStreamConsumer(), "argument1", "argument2");

        verify(logger).debug("Stdout: command output");
    }

    @Test
    public void shouldLogWarningForErrorsInStderr() {
        when(logger.isDebugEnabled()).thenReturn(true);
        CommandStreamConsumer stderr = new CommandStreamConsumer();
        String error = "some error message";
        stderr.consumeLine(error);

        commandRunner.run("echo", new CommandStreamConsumer(), stderr, "argument1", "argument2");

        verify(logger).warn("Stderr: some error message");
    }

}