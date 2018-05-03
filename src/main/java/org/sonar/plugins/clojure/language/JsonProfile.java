package org.sonar.plugins.clojure.language;

import java.util.List;

public class JsonProfile {
    private String name;
    private List<String> ruleKeys;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRuleKeys() {
        return this.ruleKeys;
    }

}
