package org.sonar.plugins.clojure.sensors.clojure;


import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.clojure.language.Clojure;

import static org.sonar.api.measures.CoreMetrics.NCLOC;

public class ClojureSensor implements Sensor {

    private static final Logger LOG = Loggers.get(ClojureSensor.class);
    public static final String SENSOR_NAME = "ClojureSensor";

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name(SENSOR_NAME).onlyOnLanguage(Clojure.KEY);
    }

    @Override
    public void execute(SensorContext context) {
        LOG.info("Running ClojureSensor");
        FilePredicates predicates = context.fileSystem().predicates();
        FilePredicate clojure = predicates.hasLanguage(Clojure.KEY);
        FilePredicate main = predicates.hasType(InputFile.Type.MAIN);

        //TODO This is inaccurate. We need to properly count the lines of code, excluding spaces, comments, etc.
        //TODO This is here to make sure analysis data will show up in the Sonar UI.
        Iterable<InputFile> sources = context.fileSystem().inputFiles(predicates.and(clojure, main));

        for (InputFile source : sources) {
            LOG.info(source.toString());
            context.<Integer>newMeasure().withValue(source.lines()).forMetric(NCLOC).on(source).save();
        }
    }
}