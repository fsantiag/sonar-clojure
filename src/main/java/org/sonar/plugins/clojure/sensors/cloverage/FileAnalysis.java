package org.sonar.plugins.clojure.sensors.cloverage;

import java.util.ArrayList;
import java.util.List;

public class FileAnalysis {
    private String path = null;
    private List<LineAnalysis> entries = new ArrayList<>();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void addLine(int number, int hits){
        entries.add(new LineAnalysis().setLineNumber(number).setHits(hits));
    }

    public List<LineAnalysis> getEntries(){
        return this.entries;
    }
}
