package org.sonar.plugins.clojure.sensors.eastwood;

import org.junit.Test;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;


import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class EastwoodIssueParserTest {

    @Test
    public void testNoIssuesGeneratedForInvalidStreamConsumer() {
        CommandStreamConsumer output = new CommandStreamConsumer();
        output.consumeLine("invalid issue");

        List<EastwoodIssue> eastwoodIssues = EastwoodIssueParser.parse(output);

        assertThat(eastwoodIssues.size(), is(0));
    }

    @Test
    public void testIssueGenerateForValidStreamConsumer() {
        CommandStreamConsumer output = new CommandStreamConsumer();
        output.consumeLine("path:1:2:some-key:description");

        List<EastwoodIssue> eastwoodIssues = EastwoodIssueParser.parse(output);

        assertThat(eastwoodIssues.size(), is(1));
        assertThat(eastwoodIssues.get(0).getLine(), is(1));
        assertThat(eastwoodIssues.get(0).getFilePath(), is("path"));
        assertThat(eastwoodIssues.get(0).getDescription(), is("description"));
        assertThat(eastwoodIssues.get(0).getExternalRuleId(), is("some-key"));
    }

    @Test
    public void testNoIssuesGeneratedForNullStreamConsumer() {
        CommandStreamConsumer output = null;

        List<EastwoodIssue> eastwoodIssues = EastwoodIssueParser.parse(output);

        assertThat(eastwoodIssues.size(), is(0));
    }

    @Test
    public void testParseRuntimeInfoForNonEmptyOutput() {
        CommandStreamConsumer output = new CommandStreamConsumer();
        output.consumeLine("== Eastwood 0.2.5 Clojure 1.8.0 JVM 1.8.0_121");

        String info = EastwoodIssueParser.parseRuntimeInfo(output);
        assertThat(info, is("Eastwood 0.2.5 Clojure 1.8.0 JVM 1.8.0_121"));
    }

    @Test
    public void testParseRuntimeInfoForEmptyOutput() {
        CommandStreamConsumer output = new CommandStreamConsumer();
        String info = EastwoodIssueParser.parseRuntimeInfo(output);
        assertNull(info);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void testParseRuntimeInfoForInvalidOutput() {
        CommandStreamConsumer output = new CommandStreamConsumer();
        output.consumeLine("==");
        // First line of the output is less than 3 chars, so substring will result in error. Keeping this exception
        // unhandled in order to fail fast in case Eastwood's output changes in future
        EastwoodIssueParser.parseRuntimeInfo(output);
    }
}