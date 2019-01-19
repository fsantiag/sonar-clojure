package org.sonar.plugins.clojure.sensors.ancient;

import org.junit.Test;
import org.sonar.plugins.clojure.sensors.CommandStreamConsumer;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class AncientOutputParserTest {

    @Test
    public void testParse() {
        CommandStreamConsumer output = new CommandStreamConsumer();
        output.consumeLine("This is some non related line which should not end to report");
        output.consumeLine("[metosin/reitit \"0.2.10\"] is available but we use \"0.2.1\"");
        output.consumeLine("[metosin/ring-http-response \"0.9.1\"] is available but we use \"0.9.0\"");

        List<OutdatedDependency> outdated = AncientOutputParser.parse(output.getData());

        assertThat(outdated.size(), is(2));
        OutdatedDependency reitit = outdated.get(0);
        OutdatedDependency expected = new OutdatedDependency();
        expected.setName("metosin/reitit");
        expected.setAvailableVersion("0.2.10");
        expected.setCurrentVersion("0.2.1");
        assertEquals(reitit, expected);
    }

    @Test
    public void testNoMatchParseCase() {
        CommandStreamConsumer output = new CommandStreamConsumer();
        output.consumeLine("This output doesnt contain any any outdated dependencies");

        List<OutdatedDependency> outdated = AncientOutputParser.parse(output.getData());

        assertThat(outdated.size(), is(0));
    }


}

