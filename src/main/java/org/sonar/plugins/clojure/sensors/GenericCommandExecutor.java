package org.sonar.plugins.clojure.sensors;

import org.sonar.api.batch.ScannerSide;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;

@ScannerSide
public class GenericCommandExecutor {

    public CommandStreamConsumer execute(String mainCommand, long timeout, String... arguments) {
        CommandStreamConsumer stdOut = new CommandStreamConsumer();
        CommandStreamConsumer stdErr = new CommandStreamConsumer();
        Command command = Command.create(mainCommand);
        for (String arg: arguments) {
            command.addArgument(arg);
        }
        CommandExecutor.create().execute(command, stdOut, stdErr, timeout);

        return stdOut;
    }
}
