package be.stijnhooft.portal.todo.model;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class TaskStatusTest {

    @Test
    public void parseWhenOpenCorrectCase() {
        assertThat(TaskStatus.parse("OPEN"), is(TaskStatus.OPEN));
    }

    @Test
    public void parseWhenOpenCorrectIncorrectCase() {
        assertThat(TaskStatus.parse("open"), is(TaskStatus.OPEN));
    }

    @Test
    public void parseWhenClosedCorrectCase() {
        assertThat(TaskStatus.parse("CLOSED"), is(TaskStatus.CLOSED));
    }

    @Test
    public void parseWhenClosedIncorrectCase() {
        assertThat(TaskStatus.parse("Closed"), is(TaskStatus.CLOSED));
    }

    @Test
    public void parseWhenDeferredCorrectCase() {
        assertThat(TaskStatus.parse("DEFERRED"), is(TaskStatus.DEFERRED));
    }

    @Test
    public void parseWhenDeferredIncorrectCase() {
        assertThat(TaskStatus.parse("DeFeRrEd"), is(TaskStatus.DEFERRED));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseWhenWeird() {
        TaskStatus.parse("weird");
    }

}
