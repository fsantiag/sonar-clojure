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

        commandExecutor.execute(cmd, stdout, stderr, TIMEOUT);

        if (logger.isDebugEnabled()) {
            logger.debug("Stdout: " + String.join(DELIMITER, stdout.getData()));
        }

        if (!stderr.getData().isEmpty()) {
            logger.warn("Stderr: " + String.join(DELIMITER, stderr.getData()));
        }

        return stdout;
    }

    public CommandStreamConsumer run(String command, String... arguments) {
        return run(command, new CommandStreamConsumer(), new CommandStreamConsumer(), arguments);
    }
}
