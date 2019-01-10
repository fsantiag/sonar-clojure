package org.sonar.plugins.clojure.sensors.cloverage;


import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.coverage.NewCoverage;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.language.ClojureLanguage;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;
import org.sonar.plugins.clojure.sensors.CommandRunner;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.sensors.Issue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CloverageSensor implements Sensor {

    private static final Logger LOG = Loggers.get(CloverageSensor.class);

    private static final String CLOVERAGE_COMMAND = "cloverage";
    private static final String LEIN_COMMAND = "lein";

    private CommandRunner commandRunner;

    public CloverageSensor(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private void saveCoverageForFile(CoverageReport report, SensorContext context) {

        for (FileAnalysis f: report.getFileEntries()) {
            LOG.debug("Processing file: " + f.getPath());
            InputFile sourceFile = getFile(f.getPath(), context.fileSystem());
            NewCoverage coverage = context.newCoverage().onFile(sourceFile);
            for (LineAnalysis l: f.getEntries()) {
                LOG.debug("Processing: " + l.getLineNumber() + " " + l.getHits());
                coverage.lineHits(l.getLineNumber(), l.getHits());
            }
            coverage.save();
        }

    }

    private InputFile getFile(String filePath, FileSystem fileSystem) {
        return fileSystem.inputFile(
                fileSystem.predicates().and(
                        fileSystem.predicates().hasRelativePath(filePath),
                        fileSystem.predicates().hasType(InputFile.Type.MAIN)));
    }


    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("SonarClojureCloverage")
                .onlyOnLanguage(ClojureLanguage.KEY)
                .global();
    }

    @Override
    public void execute(SensorContext context) {
        LOG.info("Running Cloverage");
        CommandStreamConsumer stdOut = this.commandRunner.run(LEIN_COMMAND, CLOVERAGE_COMMAND, "--codecov");

        try {
            String json = readFile("target/coverage/codecov.json", Charset.forName("UTF-8"));
            CoverageReport report = CloverageMetricParser.parse(json);
            saveCoverageForFile(report, context);
        } catch (IOException e) {
            LOG.warn("Cloverage report does not exist or cannot be read");

        }
    }

}