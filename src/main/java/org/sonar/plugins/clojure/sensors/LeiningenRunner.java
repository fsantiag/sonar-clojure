package org.sonar.plugins.clojure.sensors;

import org.sonar.api.scanner.ScannerSide;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.util.Arrays;
import java.util.Objects;

@ScannerSide
public class LeiningenRunner {

    private static final Logger LOG = Loggers.get(LeiningenRunner.class);
    private static final String DELIMITER = "\n";
    private static final String LEININGEN_COMMAND = "lein";
    private static final int SUCCESS_RETURN_CODE = 0;

    private final Logger logger;
    private final CommandExecutor commandExecutor;

    public LeiningenRunner(Logger log, CommandExecutor commandExecutor) {
        this.logger = log;
        this.commandExecutor = commandExecutor;
    }

    public LeiningenRunner() {
        this(LOG, CommandExecutor.create());
    }

    CommandStreamConsumer run(String plugin, CommandStreamConsumer stdout,
                              CommandStreamConsumer stderr, Long timeOut, String... options) {
        Command cmd = Command.create(LEININGEN_COMMAND);
        cmd.addArgument(plugin);
        Arrays.stream(options).filter(Objects::nonNull).forEach(cmd::addArgument);

        int returnCode = commandExecutor.execute(cmd, stdout, stderr, fromSecondsToMilliseconds(timeOut));

        logger.debug("plugin: " + cmd.getArguments());
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

    public CommandStreamConsumer run(Long timeOut, String command, String... pluginOptions) {
        return run(command, new CommandStreamConsumer(), new CommandStreamConsumer(), timeOut, pluginOptions);
    }
}
