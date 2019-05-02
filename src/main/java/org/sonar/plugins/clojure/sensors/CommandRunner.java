package org.sonar.plugins.clojure.sensors;

import org.sonar.api.batch.ScannerSide;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;

@ScannerSide
public class CommandRunner {

    private static final long TIMEOUT = 300_000;

    public CommandStreamConsumer run(String command, String... arguments) {
        CommandStreamConsumer stdOut = new CommandStreamConsumer();
        CommandStreamConsumer stdErr = new CommandStreamConsumer();
        Command cmd = Command.create(command);
        for (String arg: arguments) {
            if (arg != null) {
                cmd.addArgument(arg);
            }
        }
        CommandExecutor.create().execute(cmd, stdOut, stdErr, TIMEOUT);
        return stdOut;
    }
}
