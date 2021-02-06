package org.sonar.plugins.clojure.sensors;

import org.sonar.api.scanner.ScannerSide;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@ScannerSide
public class CommandRunner {

    private static final Logger STATIC_LOGGER = Loggers.get(CommandRunner.class);
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
                              CommandStreamConsumer stderr, Long timeOut, String... arguments) {
        Command cmd = Command.create(command);
        Arrays.stream(arguments).filter(Objects::nonNull).forEach(cmd::addArgument);

        int returnCode = commandExecutor.execute(cmd, stdout, stderr, fromSecondsToMilliseconds(timeOut));

        logger.debug("command: " + cmd.getArguments());
        logger.debug("stdout: " + String.join(DELIMITER, stdout.getData()));
        logger.debug("stderr: " + String.join(DELIMITER, stderr.getData()));

        if (SUCCESS_RETURN_CODE != returnCode) {
            //TODO A return code different than 0 doesn't mean error for some of the plugins we use.
            logger.warn("Command: " + cmd.getArguments() + " returned a non-zero code." +
                    " Please make sure plugin is working" +
                    " isolated before running sonar-scanner");
        }

        return stdout;
    }

    private Long fromSecondsToMilliseconds(long seconds) {
        return seconds * 1000;
    }

    public CommandStreamConsumer run(Long timeOut, String command, String... arguments) {
        return run(command, new CommandStreamConsumer(), new CommandStreamConsumer(), timeOut, arguments);
    }
}
