package org.sonar.plugins.clojure.sensors.kondo;

public class Finding {
    private String type, message, filename, level;
    private int row, col, endRow, endCol;

    public Finding(String type, String message, String filename, String level, int row, int col, int endRow, int endCol) {
        this.type = type;
        this.message = message;
        this.filename = filename;
        this.level = level;
        this.row = row;
        this.col = col;
        this.endRow = endRow;
        this.endCol = endCol;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getFilename() {
        return filename;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getEndRow() {
        return endRow;
    }

    public int getEndCol() {
        return endCol;
    }

    public String getLevel() {
        return level;
    }
}
