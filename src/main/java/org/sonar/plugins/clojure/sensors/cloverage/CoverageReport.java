package org.sonar.plugins.clojure.sensors.cloverage;

import java.util.ArrayList;
import java.util.List;

public class CoverageReport {

    private List<FileAnalysis> files = new ArrayList<>();

    public void addFile(FileAnalysis e){
        files.add(e);
    }

    public int filesCount(){
        return files.size();
    }

    public List<FileAnalysis> getFileEntries(){
        return this.files;
    }

}
