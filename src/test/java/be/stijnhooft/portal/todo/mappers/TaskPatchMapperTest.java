package be.stijnhooft.portal.todo.mappers;

import be.stijnhooft.portal.todo.model.task.Importance;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import be.stijnhooft.portal.todo.model.task.TaskStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TaskPatchMapperTest {

    @InjectMocks
    private TaskPatchMapper taskPatchMapper;

    @Test
    public void from() {
        Task subTask = new Task();
        subTask.setId("200");

        Task task = new Task();
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

        TaskPatch taskPatch = taskPatchMapper.from(task);

        assertThat(taskPatch.getDateTime(), is(ZonedDateTime.of(2019, 9, 8, 7, 6, 0, 0, ZoneId.of("UTC")).toInstant()));
        assertThat(taskPatch.getId(), is(notNullValue()));
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

}
