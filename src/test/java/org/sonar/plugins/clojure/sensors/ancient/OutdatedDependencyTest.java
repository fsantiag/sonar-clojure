package org.sonar.plugins.clojure.sensors.ancient;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OutdatedDependencyTest {

    @Test
    public void testToString() {

        OutdatedDependency outdatedDependency1 = new OutdatedDependency()
                .setName("dependency")
                .setAvailableVersion("1.0.0")
                .setCurrentVersion("0.0.1");

        assertEquals("dependency is using version: 0.0.1 but version: 1.0.0 is available.",
                outdatedDependency1.toString());
    }

    @Test
    public void testEquals() {
        OutdatedDependency outdatedDependency1 = new OutdatedDependency()
                .setName("dependency")
                .setAvailableVersion("1.0.0")
                .setCurrentVersion("0.0.1");

        OutdatedDependency outdatedDependency2 = new OutdatedDependency()
                .setName("dependency")
                .setAvailableVersion("1.0.0")
                .setCurrentVersion("0.0.1");

        assertTrue(outdatedDependency1.equals(outdatedDependency2) && outdatedDependency2.equals(outdatedDependency1));
        assertTrue(outdatedDependency1.hashCode() == outdatedDependency2.hashCode());
    }

}