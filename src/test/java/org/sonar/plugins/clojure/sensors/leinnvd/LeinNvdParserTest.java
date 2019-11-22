package org.sonar.plugins.clojure.sensors.leinnvd;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class LeinNvdParserTest {

    @Test
    public void testParse() throws IOException {
        String json = new String(Files.readAllBytes(Paths.get("src/test/resources/nvd-report.json")), UTF_8);
        List<Vulnerability> vulnerabilities = LeinNvdParser.parseJson(json);
        assertThat(vulnerabilities.size(), is(2));
        List<Vulnerability> expected = new ArrayList<>();
        expected.addAll(asList(
                new Vulnerability()
                        .setName("CVE-2018-5968")
                        .setSeverity("HIGH")
                        .setCwes("CWE-502,CWE-184")
                        .setDescription("FasterXML jackson-databind through 2.8.11 and 2.9.x through 2.9.3 allows unauthenticated remote code execution because of an incomplete fix for the CVE-2017-7525 and CVE-2017-17485 deserialization flaws. This is exploitable via two different gadgets that bypass a blacklist.")
                        .setFileName("jackson-databind-2.9.3.jar"),
                new Vulnerability()
                        .setName("CVE-2018-19362")
                        .setSeverity("CRITICAL")
                        .setCwes("CWE-502")
                        .setDescription("FasterXML jackson-databind 2.x before 2.9.8 might allow attackers to have unspecified impact by leveraging failure to block the jboss-common-core class from polymorphic deserialization.")
                        .setFileName("jackson-databind-2.9.3.jar")));
        expected.stream().forEach(entry -> assertTrue(vulnerabilities.contains(entry)));
    }


}

