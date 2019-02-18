package org.sonar.plugins.clojure.sensors.cloverage;

import org.sonar.api.batch.fs.InputFile;

import java.util.ArrayList;
import java.util.List;

public class FileAnalysis {
    private InputFile file = null;
    private List<LineAnalysis> entries = new ArrayList<>();

    public InputFile getFile() {
        return file;
    }

    public void setInputFile(InputFile file) {
        this.file = file;
    }

    public void addLine(int number, int hits){
        entries.add(new LineAnalysis().setLineNumber(number).setHits(hits));
    }

    public List<LineAnalysis> getEntries(){
        return this.entries;
    }
}
