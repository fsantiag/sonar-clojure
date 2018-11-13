package org.sonar.plugins.clojure.sensors.eastwood;

import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.sensors.Issue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EastwoodIssueParser {
    private static final Pattern EASTWOOD_PATTERN = Pattern.compile("([^:]+):(\\d+):(\\d+):([\\s\\w-]+):(.*)");

    private EastwoodIssueParser() {}

    public static String parseRuntimeInfo(CommandStreamConsumer output) {
        if (output != null) {
            List<String> lines = output.getData();
            if (lines.size() > 0) {
                // Remove the "== " prefix in eastwood's output
                return lines.get(0).substring(3);
            }
        }
        return null;
    }

    public static List<Issue> parse(CommandStreamConsumer output) {
        List<Issue> issues = new ArrayList<>();

        if (output != null) {
            for (String line : output.getData()) {
                Matcher matcher = EASTWOOD_PATTERN.matcher(line);

                if (matcher.find()) {
                    String externalRuleId = matcher.group(4);
                    String description = matcher.group(5);
                    String filePath = matcher.group(1);
                    int lineNumber = Integer.parseInt(matcher.group(2));

                    issues.add(new Issue(externalRuleId, description, filePath, lineNumber));
                }
            }
        }

        return issues;
    }
}
