package org.sonar.plugins.clojure.sensors.cloverage;

import java.util.Objects;

public class LineAnalysis {

    private int lineNumber;
    private int hits;

    public int getLineNumber() {
        return lineNumber;
    }

    public LineAnalysis setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }

    public int getHits() {
        return hits;
    }

    public LineAnalysis setHits(int hits) {
        this.hits = hits;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineAnalysis lineEntry = (LineAnalysis) o;
        return lineNumber == lineEntry.lineNumber &&
                hits == lineEntry.hits;
    }
}
