package org.sonar.plugins.clojure.leiningen;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ProjectFile {
    private static final Logger LOG = Loggers.get(ProjectFile.class);
    private List<String> contents;

    public  ProjectFile(String contents){
       this.contents = Arrays.asList(contents.split("\\r?\\n"));
    }

    public int findLineNumber(String matchToFind){
        int lineNumber = 1;
        for (String line:
             contents) {
            if (line.contains(matchToFind)){
                return lineNumber;
            }
            lineNumber++;
        }
        LOG.warn("Match not found!");
        return 1;
    }


}
