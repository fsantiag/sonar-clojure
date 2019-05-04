package org.sonar.plugins.clojure.sensors;

import org.sonar.api.batch.ScannerSide;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.util.Arrays;
import java.util.Objects;

@ScannerSide
public class CommandRunner {

    private static final Logger STATIC_LOGGER = Loggers.get(CommandRunner.class);
    private static final long TIMEOUT = 300_000;
    private static final String DELIMITER = "\n";
    private static final int SUCCESS_RETURN_CODE = 0;

    private final Logger logger;
    private final CommandExecutor commandExecutor;

    public CommandRunner(Logger log, CommandExecutor commandExecutor) {
        this.logger = log;
        this.commandExecutor = commandExecutor;
    }

    public CommandRunner() {
        this(STATIC_LOGGER, CommandExecutor.create());
    }

    CommandStreamConsumer run(String command, CommandStreamConsumer stdout,
                              CommandStreamConsumer stderr, String... arguments) {
        Command cmd = Command.create(command);
        Arrays.stream(arguments).filter(Objects::nonNull).forEach(cmd::addArgument);

        int returnCode = commandExecutor.execute(cmd, stdout, stderr, TIMEOUT);

        logger.debug("command: " + cmd.getArguments());
        logger.debug("stdout: " + String.join(DELIMITER, stdout.getData()));
        logger.debug("stderr: " + String.join(DELIMITER, stderr.getData()));

        if (SUCCESS_RETURN_CODE != returnCode) {
            logger.warn("Command: " + cmd.getArguments() + " returned a non-zero code." +
                    " Please make sure plugin is working" +
                    " isolated before running sonar-scanner");
        }

        return stdout;
    }

    public CommandStreamConsumer run(String command, String... arguments) {
        return run(command, new CommandStreamConsumer(), new CommandStreamConsumer(), arguments);
    }
}
