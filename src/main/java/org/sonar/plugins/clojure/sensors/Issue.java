package org.sonar.plugins.clojure.sensors;

public class Issue {

    private final String externalRuleId;
    private final String issueMessage;
    private final String filePath;
    private final int line;

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

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(externalRuleId);
        s.append("|");
        s.append(issueMessage);
        s.append("|");
        s.append(filePath);
        s.append("(");
        s.append(line);
        s.append(")");
        return s.toString();
    }
}
