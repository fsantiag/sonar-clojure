package org.sonar.plugins.clojure.sensors;

import org.junit.Test;

import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EastwoodIssueParserTest {
    private EastwoodIssueParser outputToIssueParser;

    @Test
    public void testNoIssuesGeneratedForInvalidStreamConsumer() {
        CommandStreamConsumer output = new CommandStreamConsumer();
        output.consumeLine("invalid issue");

        List<Issue> issues = EastwoodIssueParser.parse(output);

        assertThat(issues.size(), is(0));
    }

    @Test
    public void testIssueGenerateForValidStreamConsumer() {
        CommandStreamConsumer output = new CommandStreamConsumer();
        output.consumeLine("path:1:2:some-key:description");

        List<Issue> issues = EastwoodIssueParser.parse(output);

        assertThat(issues.size(), is(1));
        assertThat(issues.get(0).getLine(), is(1));
        assertThat(issues.get(0).getFilePath(), is("path"));
        assertThat(issues.get(0).getDescription(), is("description"));
        assertThat(issues.get(0).getExternalRuleId(), is("some-key"));
    }

    @Test
    public void testNoIssuesGeneratedForNullStreamConsumer() {
        CommandStreamConsumer output = null;

        List<Issue> issues = EastwoodIssueParser.parse(output);

        assertThat(issues.size(), is(0));
    }

}