package org.sonar.plugins.clojure.sensors.clojure;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.measure.Measure;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.plugins.clojure.language.Clojure;

import static org.assertj.core.api.Assertions.assertThat;

public class ClojureSensorTest {

    private ClojureSensor clojureSensor;

    private SensorContextTester context;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() {
        context = prepareContext();
        clojureSensor = new ClojureSensor();
    }

    @Test
    public void shouldHaveAMeasureOfNonCommentedLinesOfCodePerFile() {
        clojureSensor.execute(context);

        String fooKey = "module:foo.clj";
        String barKey = "module:bar.clj";
        Measure<Integer> fooLineCountMeasure = context.measure(fooKey, CoreMetrics.NCLOC);
        Measure<Integer> barLineCountMeasure = context.measure(barKey, CoreMetrics.NCLOC);
        assertThat(fooLineCountMeasure).isNotNull();
        assertThat(fooLineCountMeasure.value()).isEqualTo(2);

        assertThat(barLineCountMeasure).isNotNull();
        assertThat(barLineCountMeasure.value()).isEqualTo(3);
    }

    private SensorContextTester prepareContext() {
        DefaultFileSystem fileSystem = new DefaultFileSystem(temporaryFolder.getRoot());
        SensorContextTester context = SensorContextTester.create(temporaryFolder.getRoot());
        context.setFileSystem(fileSystem);

        DefaultInputFile inputFile1 = TestInputFileBuilder.create("module", "foo.clj")
                .setLanguage(Clojure.KEY)
                .initMetadata("firstLine\nsecondLine")
                .build();
        DefaultInputFile inputFile2 = TestInputFileBuilder.create("module", "bar.clj")
                .setLanguage(Clojure.KEY)
                .initMetadata("firstLine\nsecondLine\nthirdLine")
                .build();
        context.fileSystem().add(inputFile1).add(inputFile2);

        return context;
    }
}