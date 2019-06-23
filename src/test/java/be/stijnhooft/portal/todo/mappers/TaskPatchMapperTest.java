package be.stijnhooft.portal.todo.mappers;

import be.stijnhooft.portal.todo.model.Importance;
import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.model.TaskPatch;
import be.stijnhooft.portal.todo.model.TaskStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

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
        task.setExpectedDuration(Duration.ofSeconds(100));
        task.setContext("context");
        task.setDescription("description");
        task.setName("name");
        task.setSubTasks(Arrays.asList(subTask));
        task.setCreationDateTime(LocalDateTime.of(2019, 9, 8, 7, 6));

        TaskPatch taskPatch = taskPatchMapper.from(task);

        assertThat(taskPatch.getDate(), is(LocalDateTime.of(2019, 9, 8, 7, 6)));
        assertThat(taskPatch.getTaskId(), is("100"));
        assertThat(taskPatch.getChange("status"), is("OPEN"));
        assertThat(taskPatch.getChange("dueDateTime"), is("2019-10-10T10:10"));
        assertThat(taskPatch.getChange("importance"), is("I_DO_NOT_REALLY_CARE"));
        assertThat(taskPatch.getChange("startDateTime"), is("2019-11-11T11:11"));
        assertThat(taskPatch.getChange("expectedDuration"), is("PT1M40S"));
        assertThat(taskPatch.getChange("context"), is("context"));
        assertThat(taskPatch.getChange("description"), is("description"));
        assertThat(taskPatch.getChange("name"), is("name"));
        assertThat(taskPatch.getChange("subTasks"), is(nullValue()));
        assertThat(taskPatch.getChange("creationDateTime"), is("2019-09-08T07:06"));
    }

}