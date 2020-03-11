package be.stijnhooft.portal.todo.mappers;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.model.Importance;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import be.stijnhooft.portal.todo.model.task.TaskStatus;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class TaskPatchMapperTest {

    private Clock clock = Clock.fixed(ZonedDateTime.of(2019, 11, 20, 10, 0, 0, 0, ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
    private TaskPatchMapper taskPatchMapper;

    @Before
    public void init() {
        taskPatchMapper = new TaskPatchMapper(clock);
    }

    @Test
    public void fromTask() {
        var task = new Task();
        task.setId("100");
        task.setStatus(TaskStatus.OPEN);
        task.setDueDateTime(LocalDateTime.of(2019, 10, 10, 10, 10));
        task.setImportance(Importance.I_DO_NOT_REALLY_CARE);
        task.setStartDateTime(LocalDateTime.of(2019, 11, 11, 11, 11));
        task.setExpectedDurationInHours(10);
        task.setContext("context");
        task.setDescription("description");
        task.setName("name");
        task.setCreationDateTime(ZonedDateTime.of(2019, 9, 8, 7, 6, 0, 0, ZoneId.of("UTC")).toInstant());
        task.getHistory().add(new TaskPatch());

        var taskPatch = taskPatchMapper.from(task);

        assertThat(taskPatch.getId(), is(notNullValue()));
        assertThat(taskPatch.getDateTime(), is(ZonedDateTime.of(2019, 9, 8, 7, 6, 0, 0, ZoneId.of("UTC")).toInstant()));
        assertThat(taskPatch.getTaskId(), is("100"));
        assertThat(taskPatch.getChange("status"), is("OPEN"));
        assertThat(taskPatch.getChange("dueDateTime"), is("2019-10-10T10:10"));
        assertThat(taskPatch.getChange("importance"), is("I_DO_NOT_REALLY_CARE"));
        assertThat(taskPatch.getChange("startDateTime"), is("2019-11-11T11:11"));
        assertThat(taskPatch.getChange("expectedDurationInHours"), is("10"));
        assertThat(taskPatch.getChange("context"), is("context"));
        assertThat(taskPatch.getChange("description"), is("description"));
        assertThat(taskPatch.getChange("name"), is("name"));
        assertThat(taskPatch.getChange("creationDateTime"), is("2019-09-08T07:06:00Z"));
        assertThat(taskPatch.getChange("history"), is(nullValue()));
    }

    @Test
    public void fromEvent() {
        var event = new Event("Housagotchi", "Housagotchi-100", LocalDateTime.now(), new HashMap<>());

        var taskPatch = taskPatchMapper.from(event);

        assertThat(taskPatch.getId(), is(notNullValue()));
        assertThat(taskPatch.getDateTime(), is(clock.instant()));
        assertThat(taskPatch.getTaskId(), is("Housagotchi-100"));
        assertThat(taskPatch.getChange("status"), is(TaskStatus.COMPLETED.name()));
        assertThat(taskPatch.getChange("history"), is(nullValue()));
    }

}
