package org.sonar.plugins.clojure.sensors.cloverage;

import com.google.gson.*;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.IOException;
import java.util.Map;

public class CloverageMetricParser {

    private static final Logger LOG = Loggers.get(CloverageMetricParser.class);

    private CloverageMetricParser() {}

    public static CoverageReport parse(String json) throws IOException {
        CoverageReport report = new CoverageReport();
        JsonParser parser = new JsonParser();
        JsonElement jsonTree = parser.parse(json);
        JsonObject jsonObject = jsonTree.getAsJsonObject();
        JsonObject r = jsonObject.get("coverage").getAsJsonObject();

        for (Map.Entry<String, JsonElement> e:r.entrySet()) {

            FileAnalysis entry = new FileAnalysis();
            LOG.debug("Created new FileAnalysis: " + e.getKey());
            entry.setPath("src/" + e.getKey());
            // first entry in csv is line number 0 which can be discarded
            int lineNumber = 0;
            for(JsonElement i: e.getValue().getAsJsonArray()){
                if (lineNumber > 0){
                    if (!i.isJsonNull()){
                        try {
                            entry.addLine(lineNumber, i.getAsInt());
                        } catch (java.lang.NumberFormatException n) {
                            entry.addLine(lineNumber, 1);
                        }
                    };
                }
                lineNumber++;

            }
            report.addFile(entry);
        }

        return report;
    }
}
