package org.sonar.plugins.clojure.sensors.cloverage;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CloverageMetricParserTest {

    @Test
    public void testParse() throws IOException {

        String json = new String(Files.readAllBytes(Paths.get("src/test/resources/cloverage-result.json")),Charset.forName("UTF-8"));
        CoverageReport c = CloverageMetricParser.parse(json);
        assertThat(c.filesCount(), is(1));
        FileAnalysis e = c.getFileEntries().get(0);
        assertThat(e.getPath(), is("src/foo.clj"));
        List<LineAnalysis> entries = e.getEntries();
        List<LineAnalysis> expected = new ArrayList<>();
        expected.addAll(asList(new LineAnalysis().setLineNumber(1).setHits(1),

                new LineAnalysis().setLineNumber(3).setHits(1),
                new LineAnalysis().setLineNumber(5).setHits(0),
                new LineAnalysis().setLineNumber(6).setHits(1)));

        entries.stream().forEach(entry -> assertTrue(expected.contains(entry)));
    }

}
