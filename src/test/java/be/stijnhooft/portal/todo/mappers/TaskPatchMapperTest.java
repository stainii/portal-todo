package be.stijnhooft.portal.todo.mappers;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.model.domain.FlowAction;
import be.stijnhooft.portal.todo.model.Importance;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import be.stijnhooft.portal.todo.model.task.TaskStatus;
import be.stijnhooft.portal.todo.repositories.TaskRepository;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class TaskPatchMapperTest {

    private Clock clock = Clock.fixed(ZonedDateTime.of(2019, 11, 20, 10, 0, 0, 0, ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
    private TaskPatchMapper taskPatchMapper;
    private TaskRepository taskRepository;

    @Before
    public void init() {
        taskRepository = mock(TaskRepository.class);
        taskPatchMapper = new TaskPatchMapper(clock, taskRepository);
    }

    @Test
    public void mapToPatchThatCreatesATask() {
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

        var taskPatch = taskPatchMapper.mapToPatchThatCreatesATask(task);

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

        verifyNoInteractions(taskRepository);
    }

    @Test
    public void mapToTaskPatchThatCompletesATask() {
        // arrange
        var taskId = "1234567890";
        var flowId = "Housagotchi-100";

        var event = new Event("Housagotchi", flowId, FlowAction.END, LocalDateTime.now(), new HashMap<>());

        var task = new Task();
        task.setId(taskId);
        task.setFlowId(flowId);

        doReturn(Optional.of(task)).when(taskRepository).findFirstByFlowIdOrderByCreationDateTimeDesc(flowId);

        // act
        var taskPatch = taskPatchMapper.mapToTaskPatchThatCompletesATask(event).get();

        // assert
        assertThat(taskPatch.getId(), is(notNullValue()));
        assertThat(taskPatch.getDateTime(), is(clock.instant()));
        assertThat(taskPatch.getTaskId(), is(taskId));
        assertThat(taskPatch.getFlowId(), is(flowId));
        assertThat(taskPatch.getChange("status"), is(TaskStatus.COMPLETED.name()));
        assertThat(taskPatch.getChange("history"), is(nullValue()));

        verify(taskRepository).findFirstByFlowIdOrderByCreationDateTimeDesc(flowId);
    }

    @Test
    public void mapToTaskPatchThatCompletesATaskWhenNoTaskExistsForTheGivenFlowId() {
        // arrange
        var flowId = "Housagotchi-100";

        var event = new Event("Housagotchi", flowId, FlowAction.END, LocalDateTime.now(), new HashMap<>());

        doReturn(Optional.empty()).when(taskRepository).findFirstByFlowIdOrderByCreationDateTimeDesc(flowId);

        // act
        var taskPatch = taskPatchMapper.mapToTaskPatchThatCompletesATask(event);

        // assert
        assertTrue(taskPatch.isEmpty());
        verify(taskRepository).findFirstByFlowIdOrderByCreationDateTimeDesc(flowId);
    }

}
