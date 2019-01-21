package org.sonar.plugins.clojure;

import org.junit.Test;
import org.sonar.plugins.clojure.leiningen.ProjectFile;

import static org.junit.Assert.assertEquals;

public class ProjectFileTest {

    @Test
    public void lineNumberIsReturnedIfStringMatches(){
        String input = "There is match\nIn\nlast line (3) if checking for foobar";
        ProjectFile prn = new ProjectFile(input);
        assertEquals(3, prn.findLineNumber("foobar"));
    }

    @Test
    public void firstLineIsReturnedForNoMatch(){
        String input = "This\ninput\ncontains bar";
        ProjectFile prn = new ProjectFile(input);
        assertEquals(1, prn.findLineNumber("foo"));
    }
}
