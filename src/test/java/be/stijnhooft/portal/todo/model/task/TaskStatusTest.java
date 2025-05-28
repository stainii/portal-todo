package be.stijnhooft.portal.todo.model.task;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void parseWhenWeird() {
        assertThrows(IllegalArgumentException.class, () ->
            TaskStatus.parse("weird"));
    }

}
