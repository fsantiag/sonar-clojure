package org.sonar.plugins.clojure.sensors.cloverage;

import org.junit.Test;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.plugins.clojure.language.Clojure;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
        SensorContextTester context = SensorContextTester.create(new File("/"));
        // Adding file to Sonar Context
        File baseDir = new File("src/test/resources/");

        File fooSource = new File(baseDir, "foo_in_src_clj.clj");
        DefaultInputFile fooFile = TestInputFileBuilder.create("", "src/clj/foo.clj")
                .setLanguage(Clojure.KEY)
                .initMetadata(new String(Files.readAllBytes(fooSource.toPath()), StandardCharsets.UTF_8))
                .setContents(new String(Files.readAllBytes(fooSource.toPath()), StandardCharsets.UTF_8))
                .build();
        context.fileSystem().add(fooFile);

        File barSource = new File(baseDir, "bar_in_src_cljc.cljc");

        DefaultInputFile barFile = TestInputFileBuilder.create("", "src/cljc/bar.cljc")
                .setLanguage(Clojure.KEY)
                .initMetadata(new String(Files.readAllBytes(barSource.toPath()), StandardCharsets.UTF_8))
                .setContents(new String(Files.readAllBytes(barSource.toPath()), StandardCharsets.UTF_8))
                .build();

        context.fileSystem().add(barFile);

        String json = new String(Files.readAllBytes(Paths.get("src/test/resources/cloverage-result.json")),Charset.forName("UTF-8"));

        CoverageReport c = CloverageMetricParser.parse(context, json);
        assertThat(c.filesCount(), is(2));
        FileAnalysis e = c.getFileEntries().get(0);

        List<LineAnalysis> entries = e.getEntries();
        List<LineAnalysis> expected = new ArrayList<>();
        expected.addAll(asList(new LineAnalysis().setLineNumber(1).setHits(1),

                new LineAnalysis().setLineNumber(3).setHits(1),
                new LineAnalysis().setLineNumber(5).setHits(0),
                new LineAnalysis().setLineNumber(6).setHits(1)));

        entries.stream().forEach(entry -> assertTrue(expected.contains(entry)));
    }

}
