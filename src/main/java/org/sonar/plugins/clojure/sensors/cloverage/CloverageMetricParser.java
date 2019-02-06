package org.sonar.plugins.clojure.sensors.cloverage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.settings.ClojureProperties;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class CloverageMetricParser {

    private static final Logger LOG = Loggers.get(CloverageMetricParser.class);

    private CloverageMetricParser() {
    }

    private static Optional<FileAnalysis> findFileBySources(SensorContext context, String filename) {
        FileSystem fs = context.fileSystem();
        FilePredicate pattern = fs.predicates().matchesPathPattern("**/" + filename);
        InputFile potentialFile = fs.inputFile(pattern);


        if (potentialFile != null) {
            LOG.debug("Found file");
            FileAnalysis foundSource = new FileAnalysis();
            foundSource.setInputFile(potentialFile);
            return Optional.of(foundSource);
        }
        ;

        return Optional.empty();
    }

    public static CoverageReport parse(SensorContext context, String json) {
        LOG.debug("Running CoverageReport parser");
        CoverageReport report = new CoverageReport();
        JsonParser parser = new JsonParser();
        JsonElement jsonTree = parser.parse(json);
        JsonObject jsonObject = jsonTree.getAsJsonObject();
        JsonObject r = jsonObject.get("coverage").getAsJsonObject();


        for (Map.Entry<String, JsonElement> e : r.entrySet()) {


            LOG.debug("Created new FileAnalysis: " + e.getKey());

            Optional<FileAnalysis> fileAnalysisOptional = findFileBySources(context, e.getKey());

            if (fileAnalysisOptional.isPresent()) {
                FileAnalysis fileAnalysis = fileAnalysisOptional.get();
                // first entry in csv is line number 0 which can be discarded
                int lineNumber = 0;
                for (JsonElement i : e.getValue().getAsJsonArray()) {
                    if (lineNumber > 0) {
                        if (!i.isJsonNull()) {
                            try {
                                fileAnalysis.addLine(lineNumber, i.getAsInt());
                            } catch (NumberFormatException n) {
                                fileAnalysis.addLine(lineNumber, 1);
                            }
                        }
                    }
                    lineNumber++;

                }
                report.addFile(fileAnalysis);
            } else {
                LOG.warn("Namespace: " + e.getKey() + " cannot be found. Check  property");
            }
        }

        return report;
    }
}
