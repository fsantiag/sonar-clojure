package org.sonar.plugins.clojure.sensors;

import org.sonar.api.utils.command.StreamConsumer;

import java.util.LinkedList;
import java.util.List;

public class CommandStreamConsumer implements StreamConsumer {
    private List<String> data = new LinkedList<>();

    @Override
    public void consumeLine(String line) {
        data.add(line);
    }

    public List<String> getData() {
        return data;
    }
}
