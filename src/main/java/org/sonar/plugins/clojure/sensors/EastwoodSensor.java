package org.sonar.plugins.clojure.sensors;


import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.language.ClojureLanguage;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EastwoodSensor implements Sensor {

    private static final Logger LOG = Loggers.get(EastwoodSensor.class);

    private FileSystem fileSystem;

    private ArrayList<Issue> issues;

    public EastwoodSensor(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    private static String runCommand(String dir, String leinCmd) {

        String cmdStr = "cd " + dir + "\n" + leinCmd;
        String[] cmd = {"/bin/sh", "-c", cmdStr};
        String result = null;
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                LOG.info(inputLine);
                result += inputLine + "\n";
            }
            in.close();

        } catch (IOException e) {
            LOG.error("Parsing exception", e);

        } catch (Exception e) {
            LOG.error("Parsing exception", e);
        }
        return result;
    }

    private void getResourceAndSaveIssue(final Issue issues, SensorContext sensorContext) {
        InputFile inputFile = fileSystem.inputFile(
                fileSystem.predicates().and(
                        fileSystem.predicates().hasRelativePath(issues.getFilePath()),
                        fileSystem.predicates().hasType(InputFile.Type.MAIN)));

        if (inputFile != null) {
            saveIssue(inputFile, issues.getLine(), issues.getExternalRuleId(), issues.getDescription(), sensorContext);
        } else {
            LOG.error("Not able to find a InputFile with " + issues.getFilePath());
        }
    }

    private void saveIssue(final InputFile inputFile, int line, final String externalRuleKey, final String message, SensorContext sensorContext) {
        RuleKey ruleKey = RuleKey.of(ClojureLintRulesDefinition.REPOSITORY_KEY, externalRuleKey.trim());

        NewIssue newIssue = sensorContext.newIssue()
                .forRule(ruleKey);

        NewIssueLocation primaryLocation = newIssue.newLocation()
                .on(inputFile)
                .message(message.trim());
        if (line > 0) {
            primaryLocation.at(inputFile.selectLine(line));
        }
        newIssue.at(primaryLocation);

        newIssue.save();
    }

    public void analyse(SensorContext sensorContext) {
        LOG.info("Clojure project detected, running sonar-clojure");
        LOG.info("Running Eastwood");

        buildEastwoodLintProperties();

        LOG.info("Saving measures");

        for (Issue issue : issues) {

            getResourceAndSaveIssue(issue, sensorContext);
        }

    }

    private void buildEastwoodLintProperties() {
        String baseDirectory = fileSystem.baseDir().toString();
        String output = "";
        issues = new ArrayList<>();

        try {
                Pattern p = Pattern.compile("[^:]+:\\d+:\\d+:.*");
            output = runCommand(baseDirectory, "lein eastwood");
            BufferedReader buffer = new BufferedReader(new StringReader(output));
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = buffer.readLine()) != null) {
                Matcher m = p.matcher(line);
                while (m.find()) {
                    lines.add(m.group());
                }
            }
            for (String temp : lines) {
                String[] tokens = temp.split(":");

                issues.add(new Issue(tokens[3], tokens[4], tokens[0], Integer.valueOf(tokens[1])));
            }
        } catch (IOException e) {
            LOG.error("EASTWOOD IO EXCEPTION", e);
        } catch (Exception e) {
            LOG.error("EASTWOOD EXCEPTION", e);

        }
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("SonarClojure")
                .onlyOnLanguage(ClojureLanguage.KEY)
                .global();
    }

    @Override
    public void execute(SensorContext sensorContext) {
        this.analyse(sensorContext);
    }

}