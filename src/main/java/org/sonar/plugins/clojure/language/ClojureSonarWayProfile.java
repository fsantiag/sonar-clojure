package org.sonar.plugins.clojure.language;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;

import java.io.FileNotFoundException;
import java.io.FileReader;


public final class ClojureSonarWayProfile implements BuiltInQualityProfilesDefinition {

    public static final String CLOJURE_SONAR_WAY_PATH = "src/main/resources/clojure/sonar_way.json";

    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("Sonar way", ClojureLanguage.KEY);
        profile.setDefault(true);

        JsonProfile jsonProfile = new JsonProfile();

        try {
            jsonProfile = readProfile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        jsonProfile.getRuleKeys().stream()
                .forEach(key -> profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, key));

        profile.done();
    }

    private JsonProfile readProfile() throws FileNotFoundException {
        JsonReader reader = new JsonReader(new FileReader(CLOJURE_SONAR_WAY_PATH));
        return new Gson().fromJson(reader, JsonProfile.class);
    }

}