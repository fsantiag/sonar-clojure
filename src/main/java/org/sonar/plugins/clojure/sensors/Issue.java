package org.sonar.plugins.clojure.sensors;

public class Issue {

    private String externalRuleId;
    private String issueMessage;
    private String filePath;
    private int line;

    public Issue(String externalRuleId, String description, String filePath, int line) {
        this.externalRuleId = externalRuleId;
        this.issueMessage = description;
        this.filePath = filePath;
        this.line = line;
    }

    public String getExternalRuleId() {
        return externalRuleId;
    }

    public String getDescription() {
        return issueMessage;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getLine() {
        return line;
    }

}
