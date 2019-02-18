package org.sonar.plugins.clojure.sensors.ancient;

import java.util.Objects;

public class OutdatedDependency {

    private String name;
    private String currentVersion;
    private String availableVersion;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OutdatedDependency that = (OutdatedDependency) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(currentVersion, that.currentVersion) &&
                Objects.equals(availableVersion, that.availableVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, currentVersion, availableVersion);
    }

    @Override
    public String toString() {
        return name + " is using version: " +
                getCurrentVersion() + " but version: " +
                getAvailableVersion() + " is available.";
    }

    public String getName() {
        return name;
    }

    public OutdatedDependency setName(String name) {
        this.name = name;
        return this;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public OutdatedDependency setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
        return this;
    }

    public String getAvailableVersion() {
        return availableVersion;
    }

    public OutdatedDependency setAvailableVersion(String availableVersion) {
        this.availableVersion = availableVersion;
        return this;
    }
}
