package org.sonar.plugins.clojure.sensors.kibit;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.sensors.Issue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KibitIssueParser {
    private static final Pattern KIBIT_START = Pattern.compile("----");
    private static final Pattern KIBIT_ENTRY = Pattern.compile("At\\s([^:]+):([^`]+):");
    private KibitIssueParser() {
    }

    private static boolean isEntryStart(String row) {
        return KIBIT_START.matcher(row).find();
    }

    public static List<Issue> parse(CommandStreamConsumer output) {
        List<Issue> issues = new ArrayList<>();
        if (output != null) {
            List<String> kibitReport = output.getData();
            ArrayDeque<String> filteredReport = kibitReport.stream()
                    .filter(line -> !line.isEmpty())
                    .filter(line -> !isEntryStart(line))
                    .collect(Collectors.toCollection(ArrayDeque::new));

            while (!filteredReport.isEmpty()) {
                String path = filteredReport.pop();
                Matcher matcher = KIBIT_ENTRY.matcher(path);
                if (matcher.find()) {
                    String filename = matcher.group(1);
                    // Kibit may return line number sometimes as string "null"
                    int lineNumber = !matcher.group(2).equals("null") ? Integer.parseInt(matcher.group(2)) : 1;
                    StringBuilder description = new StringBuilder();
                    while (!filteredReport.isEmpty() && !KIBIT_ENTRY.matcher(filteredReport.peek()).find()) {
                        description.append(filteredReport.pop()).append("\n");
                    }
                    issues.add(new Issue("kibit", description.toString(), filename, lineNumber));
                }
            }
        }
        return issues;
    }
}
