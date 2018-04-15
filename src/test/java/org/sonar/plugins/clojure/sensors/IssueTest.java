package org.sonar.plugins.clojure.sensors;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class IssueTest {

    private Issue issue;

    @Before
    public void setUp() throws Exception {
        issue = new Issue(
                "some-rule-id",
                "This is a message!",
                "/path/to/file",
                10
        );
    }

    @Test
    public void testGetIssueType() {
        assertThat(issue.getExternalRuleId(), is("some-rule-id"));
    }

    @Test
    public void testGetDescription() {
        assertThat(issue.getDescription(), is("This is a message!"));
    }

    @Test
    public void testGetFilePath() {
        assertThat(issue.getFilePath(), is("/path/to/file"));
    }

    @Test
    public void testGetLine() {
        assertThat(issue.getLine(), is(10));
    }
}