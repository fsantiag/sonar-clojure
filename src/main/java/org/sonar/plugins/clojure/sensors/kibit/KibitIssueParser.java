package org.sonar.plugins.clojure.sensors.kibit;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.sensors.Issue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KibitIssueParser {
    private static final Pattern KIBIT_START = Pattern.compile("----");
    private static final Pattern KIBIT_ENTRY = Pattern.compile("##### `([^:]+):([^`]+)`");
    private static final Logger LOG = Loggers.get(KibitIssueParser.class);
    private KibitIssueParser() {
    }

    private static boolean isEntryStart(String row) {
        return KIBIT_START.matcher(row).find();
    }

    public static List<Issue> parse(CommandStreamConsumer output) {
        List<Issue> issues = new ArrayList<>();

        if (output != null) {
            List<String> result = output.getData();
            for (int i = 0; i < result.size(); i++) {
                if (isEntryStart(result.get(i))) {
                    i++;
                    Matcher matcher = KIBIT_ENTRY.matcher(result.get(i));
                    if (matcher.find()) {
                        String filename = matcher.group(1);
                        int line = 1;
                        // Kibit may return line number sometimes as string "null"
                        if (!matcher.group(2).equals("null")) {
                            line = Integer.parseInt(matcher.group(2));
                        }
                        KibitEntry e = new KibitEntry(filename, line);
                        while (i < result.size() - 1) {
                            i++;
                            if (isEntryStart(result.get(i))) {
                                i--;
                                break;
                            } else {
                                e.addEntry(result.get(i));
                            }
                        }
                        issues.add(new Issue("kibit", e.descriptionToString(), e.getFile(), e.getLine()));
                    }
                }
            }
        }

        return issues;
    }
}
