package org.sonar.plugins.clojure.sensors.kibit;

import org.junit.Test;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;
import org.sonar.plugins.clojure.sensors.Issue;
import org.sonar.plugins.clojure.sensors.eastwood.EastwoodIssueParser;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class KibitIssueParserTest {

    @Test
    public void testNoIssuesGeneratedForInvalidStreamConsumer() {
        CommandStreamConsumer output = new CommandStreamConsumer();
        output.consumeLine("invalid issue");

        List<Issue> issues = KibitIssueParser.parse(output);

        assertThat(issues.size(), is(0));
    }

    @Test
    public void testIssueGenerateForValidStreamConsumer() {
        CommandStreamConsumer output = new CommandStreamConsumer();
        output.consumeLine("----");
        output.consumeLine("##### `src/sok_register_api/boa.clj:45`");
        output.consumeLine("Consider using:...");
        output.consumeLine("some consideration");
        output.consumeLine("----");
        output.consumeLine("##### `src/sok_register_api/something.clj:67`");
        output.consumeLine("Consider something else");
        List<Issue> issues = KibitIssueParser.parse(output);

        assertThat(issues.size(), is(2));
        assertThat(issues.get(0).getLine(), is(45));
        assertThat(issues.get(0).getFilePath(), is("src/sok_register_api/boa.clj"));
        assertThat(issues.get(0).getDescription(), is("Consider using:..." + "\n" + "some consideration\n"));
        assertThat(issues.get(0).getExternalRuleId(), is("kibit"));

        assertThat(issues.get(1).getLine(), is(67));
    }

    @Test
    public void testNoIssuesGeneratedForNullStreamConsumer() {
        CommandStreamConsumer output = null;

        List<Issue> issues = KibitIssueParser.parse(output);

        assertThat(issues.size(), is(0));
    }
}

