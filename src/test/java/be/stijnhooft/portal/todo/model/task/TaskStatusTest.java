package be.stijnhooft.portal.todo.model.task;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
    public void parseWhenCompletedCorrectCase() {
        assertThat(TaskStatus.parse("COMPLETED"), is(TaskStatus.COMPLETED));
    }

    @Test
    public void parseWhenCompletedIncorrectCase() {
        assertThat(TaskStatus.parse("Completed"), is(TaskStatus.COMPLETED));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseWhenWeird() {
        TaskStatus.parse("weird");
    }

}
