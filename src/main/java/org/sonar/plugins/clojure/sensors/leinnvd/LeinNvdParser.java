package org.sonar.plugins.clojure.sensors.leinnvd;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.util.ArrayList;
import java.util.List;

public class LeinNvdParser {
    private static final Logger LOG = Loggers.get(LeinNvdParser.class);

    private LeinNvdParser(){}

    public static List<Vulnerability> parseJson(String json){
        List<Vulnerability> vulnerabilities = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonElement jsonTree = parser.parse(json);
        JsonObject jsonObject = jsonTree.getAsJsonObject();
        JsonArray dependencies = jsonObject.get("dependencies").getAsJsonArray();

        for (JsonElement element: dependencies) {
            JsonObject dependency = element.getAsJsonObject();

            if (dependency.has("vulnerabilities")){
                LOG.debug("Found vulnerabilities in: " + dependency.getAsJsonPrimitive("fileName").getAsString());
                JsonArray dependencyVulnerabilities = dependency.get("vulnerabilities").getAsJsonArray();
                for (JsonElement dependencyVulnerability: dependencyVulnerabilities) {
                    JsonObject dependencyVulnerabilityObject = dependencyVulnerability.getAsJsonObject();
                    Vulnerability v = new Vulnerability()
                            .setName(dependencyVulnerabilityObject.getAsJsonPrimitive("name").getAsString())
                            .setSeverity(dependencyVulnerabilityObject.getAsJsonPrimitive("severity").getAsString())
                            .setCwe(dependencyVulnerabilityObject.getAsJsonPrimitive("cwe").getAsString())
                            .setDescription(dependencyVulnerabilityObject.getAsJsonPrimitive("description").getAsString())
                            .setFileName(dependency.getAsJsonPrimitive("fileName").getAsString());
                    vulnerabilities.add(v);
                }
            }
        }
        return vulnerabilities;
    }
}
