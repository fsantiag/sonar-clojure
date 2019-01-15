package org.sonar.plugins.clojure.sensors.leinNvd;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
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
                        .setName("CVE-2017-7656")
                        .setSeverity("Medium")
                        .setCwe("CWE-284 Improper Access Control")
                        .setDescription("In Eclipse Jetty, versions 9.2.x and older, 9.3.x (all configurations), and 9.4.x (non-default configuration with RFC2616 compliance enabled), HTTP/0.9 is handled poorly. An HTTP/1 style request line (i.e. method space URI space version) that declares a version of HTTP/0.9 was accepted and treated as a 0.9 request. If deployed behind an intermediary that also accepted and passed through the 0.9 version (but did not act on it), then the response sent could be interpreted by the intermediary as HTTP/1 headers. This could be used to poison the cache if the server allowed the origin client to generate arbitrary content in the response.")
                        .setFileName("jetty-util-9.2.21.v20170120.jar"),
                new Vulnerability()
                        .setName("CVE-2017-7657")
                        .setSeverity("High")
                        .setCwe("CWE-190 Integer Overflow or Wraparound")
                        .setDescription("In Eclipse Jetty, versions 9.2.x and older, 9.3.x (all configurations), and 9.4.x (non-default configuration with RFC2616 compliance enabled), transfer-encoding chunks are handled poorly. The chunk length parsing was vulnerable to an integer overflow. Thus a large chunk size could be interpreted as a smaller chunk size and content sent as chunk body could be interpreted as a pipelined request. If Jetty was deployed behind an intermediary that imposed some authorization and that intermediary allowed arbitrarily large chunks to be passed on unchanged, then this flaw could be used to bypass the authorization imposed by the intermediary as the fake pipelined request would not be interpreted by the intermediary as a request.")
                        .setFileName("jetty-util-9.2.21.v20170120.jar")));
        expected.stream().forEach(entry -> assertTrue(vulnerabilities.contains(entry)));
    }


}

