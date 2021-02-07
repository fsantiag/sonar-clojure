package org.sonar.plugins.clojure.sensors.kondo;

import org.junit.Test;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class KondoIssueParserTest {

    @Test
    public void testNoIssuesGeneratedForInvalidStreamConsumer() {
        CommandStreamConsumer output = new CommandStreamConsumer();
        output.consumeLine("{:findings [], :summary {:error 0, :warning 0, :info 0, :type :summary, :duration 173, :files 32}}");
        List<Finding> issues = KondoIssueParser.parse(output);
        assertThat(issues.size(), is(0));
    }

    @Test
    public void testIssueGenerateForValidStreamConsumer() {
        CommandStreamConsumer output = new CommandStreamConsumer();
        output.consumeLine(
                "{:findings [{:type :redundant-let, :message \"Redundant let expression.\", :level :warning, " +
                        ":row 35, :end-row 36, :end-col 15, :col 5, :filename \"src/example/init.clj\"}], " +
                        ":summary {:error 0, :warning 1, :info 0, :type :summary, :duration 157, :files 32}}");

        List<Finding> issues = KondoIssueParser.parse(output);

        assertThat(issues.size(), is(1));
        assertThat(issues.get(0).getMessage(), is("Redundant let expression."));
        assertThat(issues.get(0).getType(), is("redundant-let"));
        assertThat(issues.get(0).getFilename(), is("src/example/init.clj"));
        assertThat(issues.get(0).getLevel(), is("warning"));
        assertThat(issues.get(0).getCol(), is(5));
        assertThat(issues.get(0).getEndCol(), is(15));
        assertThat(issues.get(0).getRow(), is(35));
        assertThat(issues.get(0).getEndRow(), is(36));
    }

    @Test
    public void testNoIssuesGeneratedForNullStreamConsumer() {
        CommandStreamConsumer output = null;
        List<Finding> issues = KondoIssueParser.parse(output);
        assertThat(issues.size(), is(0));
    }
}