package org.sonar.plugins.clojure.language;

import com.google.gson.Gson;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.plugins.clojure.rules.ClojureLintRulesDefinition;

import java.io.InputStream;
import java.io.InputStreamReader;


public final class ClojureSonarWayProfile implements BuiltInQualityProfilesDefinition {

    public static final String CLOJURE_SONAR_WAY_PATH = "/clojure/sonar_way.json";

    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("Sonar way", ClojureLanguage.KEY);
        profile.setDefault(true);

        JsonProfile jsonProfile = readProfile();

        jsonProfile.getRuleKeys()
                .forEach(key -> profile.activateRule(ClojureLintRulesDefinition.REPOSITORY_KEY, key));

        profile.done();
    }

    private JsonProfile readProfile() {
        InputStream resourceAsStream = getClass().getResourceAsStream(CLOJURE_SONAR_WAY_PATH);
        InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
        return new Gson().fromJson(inputStreamReader, JsonProfile.class);
    }

}