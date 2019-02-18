package org.sonar.plugins.clojure.sensors.ancient;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AncientOutputParser {

    private static final Pattern ANCIENT_PATTERN = Pattern.compile("\\[([^\\s]+)\\s\"([^\"]+)\"]([^\"]+)\"([^\"]+)\"");

    private AncientOutputParser() {}

    private static Matcher parseString(String str){
        Matcher matcher = ANCIENT_PATTERN.matcher(str);
        matcher.find();
        return matcher;
    }

    private static boolean removeNonMatches(String str){
        Matcher matcher = ANCIENT_PATTERN.matcher(str);
        return matcher.find();

    }

    public static List<OutdatedDependency> parse(List<String> output){

        return output.stream()
                .map(e -> e.replaceAll("\u001B\\[[;\\d]*m", ""))
                .filter(AncientOutputParser::removeNonMatches)
                .map(AncientOutputParser::parseString)
                .map(dep -> new OutdatedDependency()
                        .setName(dep.group(1))
                        .setCurrentVersion(dep.group(4))
                        .setAvailableVersion(dep.group(2)))
                .collect(Collectors.toList());
    }
}
