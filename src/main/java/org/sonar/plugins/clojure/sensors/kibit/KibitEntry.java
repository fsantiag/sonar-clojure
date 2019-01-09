package org.sonar.plugins.clojure.sensors.kibit;

import java.util.ArrayList;
import java.util.List;

public class KibitEntry {

    private String file;
    private int line;
    private List<String> description;

    public String getFile() {
        return file;
    }

    public int getLine() {
        return line;
    }

    public KibitEntry(String file, int line){
        this.file = file;
        this.line = line;
        this.description = new ArrayList<>();
    }

    public void addEntry(String line){
        this.description.add(line);
    }

    public String descriptionToString(){
        StringBuilder str = new StringBuilder();
        for (String d:description) {
            str.append(d);
            str.append("\n");
        }
        return str.toString();
    }

    @Override
    public  String toString(){
        StringBuilder str = new StringBuilder();
        str.append("File: " );
        str.append(file);
        str.append("Line: " );
        str.append(line);
        str.append(this.descriptionToString());
        return str.toString();
    }

}
