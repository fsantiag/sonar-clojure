package org.sonar.plugins.clojure.sensors.cloverage;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CloverageMetricParserTest {

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
    @Test
    public void testParse() throws IOException {
        String json = readFile("src/test/java/org/sonar/plugins/clojure/sensors/cloverage/cloverage-result.json", Charset.forName("UTF-8"));
        CoverageReport c = CloverageMetricParser.parse(json);
        assertThat(c.filesCount(), is(1));
        FileAnalysis e = c.getFileEntries().get(0);
        assertThat(e.getPath(), is("src/sok_register_api/config.clj"));
        List<LineAnalysis> entries = e.getEntries();
        List<LineAnalysis> expected = new ArrayList<>();
        expected.addAll(asList(new LineAnalysis().setLineNumber(1).setHits(1),

                new LineAnalysis().setLineNumber(3).setHits(1),

                new LineAnalysis().setLineNumber(5).setHits(0),
                new LineAnalysis().setLineNumber(6).setHits(1)));

        entries.stream().forEach(entry -> assertTrue(expected.contains(entry)));
    }

}
