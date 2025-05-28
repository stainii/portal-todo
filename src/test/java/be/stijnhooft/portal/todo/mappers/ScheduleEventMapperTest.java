package be.stijnhooft.portal.todo.mappers;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import be.stijnhooft.portal.todo.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static be.stijnhooft.portal.todo.PortalTodoApplication.APPLICATION_NAME;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
public class ScheduleEventMapperTest {

    @Mock
    private TaskRepository taskRepository;

    private Clock clock = Clock.fixed(ZonedDateTime.of(2019, 10, 10, 10, 10, 10, 10, ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

    private ScheduleEventMapper scheduleEventMapper;

    @BeforeEach
    public void init() {
        scheduleEventMapper = new ScheduleEventMapper(taskRepository, clock);
    }

    @Test
    public void mapWhenTaskIsNull() {
        assertThrows(NullPointerException.class, () ->
                scheduleEventMapper.map(null));
    }

    @Test
    public void mapWhenDueDateIsNull() {
        // arrange
        Task task = new Task();
        task.setId("12");
        task.setFlowId(APPLICATION_NAME + "-12");
        task.setName("test name");

        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setTaskId("12");
        taskPatch.setFlowId(APPLICATION_NAME + "-12");
        taskPatch.addChange("name", "test name");

        doReturn(Optional.of(task)).when(taskRepository).findById("12");

        // act
        Event event = scheduleEventMapper.map(taskPatch);

        // assert
        verify(taskRepository).findById("12");
        verifyNoMoreInteractions(taskRepository);

        assertThat(event.getSource(), is(APPLICATION_NAME));
        assertThat(event.getFlowId(), is(APPLICATION_NAME + "-12"));
        assertThat(event.getPublishDate(), is(LocalDateTime.of(2019, 10, 10, 10, 10, 10, 10)));
        assertThat(event.getData().keySet(), hasItem("type"));
        assertThat(event.getData().get("type"), is("schedule"));
        assertThat(event.getData().keySet(), hasItem("task"));
        assertThat(event.getData().get("task"), is("test name"));
        assertThat(event.getData().keySet(), not(hasItem("dueDate")));
    }

    @Test
    public void mapWhenDueDateIsFilledIn() {
        // arrange
        Task task = new Task();
        task.setId("12");
        task.setFlowId(APPLICATION_NAME + "-12");
        task.setName("test name");
        task.setDueDateTime(LocalDateTime.of(2019, 6, 7, 10, 12, 13));

        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setTaskId("12");
        taskPatch.setFlowId(APPLICATION_NAME + "-12");
        taskPatch.addChange("name", "test name");

        doReturn(Optional.of(task)).when(taskRepository).findById("12");

        // act
        Event event = scheduleEventMapper.map(taskPatch);

        // assert
        verify(taskRepository).findById("12");
        verifyNoMoreInteractions(taskRepository);

        assertThat(event.getSource(), is(APPLICATION_NAME));
        assertThat(event.getFlowId(), is(APPLICATION_NAME + "-12"));
        assertThat(event.getPublishDate(), is(LocalDateTime.of(2019, 10, 10, 10, 10, 10, 10)));
        assertThat(event.getData().keySet(), hasItem("type"));
        assertThat(event.getData().get("type"), is("schedule"));
        assertThat(event.getData().keySet(), hasItem("task"));
        assertThat(event.getData().get("task"), is("test name"));
        assertThat(event.getData().keySet(), hasItem("dueDate"));
        assertThat(event.getData().get("dueDate"), is("2019-06-07T10:12:13"));
    }

    @Test
    public void mapWhenInvalidTaskId() {
        assertThrows(IllegalArgumentException.class, () -> {
            // arrange
            Task task = new Task();
            task.setId("120");
            task.setName("test name");
            task.setDueDateTime(LocalDateTime.of(2019, 6, 7, 10, 12, 13));

            TaskPatch taskPatch = new TaskPatch();
            taskPatch.setTaskId("12");
            taskPatch.addChange("name", "test name");

            doReturn(Optional.empty()).when(taskRepository).findById("12");

            // act
            Event event = scheduleEventMapper.map(taskPatch);

            // assert
            verify(taskRepository).findById("12");
            verifyNoMoreInteractions(taskRepository);
        });
    }
}
