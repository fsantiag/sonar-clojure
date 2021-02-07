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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LeiningenRunnerTest {

    @Mock
    private Logger logger;

    @Mock
    private CommandExecutor commandExecutor;

    private LeiningenRunner leiningenRunner;

    @Before
    public void setUp() {
        leiningenRunner = new LeiningenRunner(logger, commandExecutor);
    }

    @Test
    public void shouldTakeMultipleArgumentsForLeinPlugin() {
        leiningenRunner.run(300L, "eastwood", "argument1", "argument2");

        ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(commandExecutor, times(1))
                .execute(
                        commandCaptor.capture(),
                        any(CommandStreamConsumer.class),
                        any(CommandStreamConsumer.class),
                        anyLong());

        Command cmd = commandCaptor.getValue();
        assertThat(cmd.getExecutable(), is("lein"));
        assertThat(cmd.getArguments().size(), is(3));
        assertThat(cmd.getArguments().get(0), is("eastwood"));
        assertThat(cmd.getArguments().get(1), is("argument1"));
        assertThat(cmd.getArguments().get(2), is("argument2"));
    }

    @Test
    public void shouldLogStdoutStderrAndCommandInDebugMode() {
        CommandStreamConsumer stdout = new CommandStreamConsumer();
        stdout.consumeLine("line in stdout");
        CommandStreamConsumer stderr = new CommandStreamConsumer();
        stderr.consumeLine("line in stderr");

        leiningenRunner.run(
                "eastwood",
                stdout,
                stderr,
                300L,
                "Linux",
                "argument1",
                "argument2"
        );

        verify(logger, times(1)).debug("plugin: [eastwood, argument1, argument2]");
        verify(logger, times(1)).debug("stdout: line in stdout");
        verify(logger, times(1)).debug("stderr: line in stderr");
    }

    @Test
    public void shouldLogErrorForReturnCodeDifferentThanZero() {
        when(commandExecutor.execute(any(),any(),any(),anyLong())).thenReturn(1);
        CommandStreamConsumer dummyStreamConsumer = new CommandStreamConsumer();

        leiningenRunner.run(
                "eastwood",
                dummyStreamConsumer,
                dummyStreamConsumer,
                300L,
                "Linux",
                "argument1",
                "argument2"
        );
        verify(logger, times(1)).warn(
                "Command: [eastwood, argument1, argument2] returned a non-zero " +
                "code. Please make sure plugin is working isolated before running sonar-scanner");
    }

    @Test
    public void shouldUseBatFileWhenOperatingSystemIsWindows() {
        CommandStreamConsumer stdout = new CommandStreamConsumer();
        CommandStreamConsumer stderr = new CommandStreamConsumer();
        when(commandExecutor.execute(any(),any(),any(),anyLong())).thenReturn(0);

        leiningenRunner.run("eastwood", stdout, stderr, 300L, "windows");

        ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(commandExecutor).execute(commandCaptor.capture(), any(CommandStreamConsumer.class), any(CommandStreamConsumer.class), anyLong());
        Command cmd = commandCaptor.getValue();
        assertThat(cmd.getExecutable(), is("lein.bat"));
    }

}