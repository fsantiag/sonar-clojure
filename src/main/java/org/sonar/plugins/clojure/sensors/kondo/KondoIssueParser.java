package org.sonar.plugins.clojure.sensors.kondo;

import org.sonar.api.internal.google.common.collect.Lists;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import us.bpsm.edn.Keyword;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static us.bpsm.edn.Keyword.newKeyword;
import static us.bpsm.edn.parser.Parsers.defaultConfiguration;

public class KondoIssueParser {
    public static List<Finding> parse(CommandStreamConsumer stdOut) {
        if (stdOut == null){
            return Collections.EMPTY_LIST;
        }
        String kondoOutput = String.join("", stdOut.getData());

        Parseable parseable = Parsers.newParseable(kondoOutput);
        Parser parser = Parsers.newParser(defaultConfiguration());

        Map<?, ?> m = (Map<?, ?>) parser.nextValue(parseable);

        List<Object> findings = (List<Object>) m.get(newKeyword("findings"));

        return findings.stream()
                .map(f -> asFinding((Map<?, ?>) f))
                .collect(Collectors.toList());
    }

    private static Finding asFinding(Map<?, ?> finding) {
        String message = (String) finding.get(newKeyword("message"));
        String filename = (String) finding.get(newKeyword("filename"));
        String level = ((Keyword) finding.get(newKeyword("level"))).getName();
        Long row = (Long) finding.get(newKeyword("row"));
        Long col = (Long) finding.get(newKeyword("col"));
        Long endRow = (Long) finding.get(newKeyword("end-row"));
        Long endCol = (Long) finding.get(newKeyword("end-col"));

        return new Finding("kondo", message, filename, level,
                row.intValue(), col.intValue(), endRow.intValue(), endCol.intValue());
    }
}
