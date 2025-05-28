package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.dtos.Source;
import be.stijnhooft.portal.todo.messaging.EventPublisher;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import be.stijnhooft.portal.todo.model.task.TaskPatchResult;
import be.stijnhooft.portal.todo.model.task.TaskStatus;
import be.stijnhooft.portal.todo.repositories.TaskPatchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
public class TaskPatchServiceTest {

    @Mock
    private TaskPatchRepository taskPatchRepository;

    @Mock
    private TaskService taskService;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private TaskPatchService taskPatchService;

    @Test
    public void patchATaskThatHasBeenCompletedAndSourceIsUser() {
        // arrange
        Task task = new Task();
        task.setId("10");
        task.setStatus(TaskStatus.OPEN);
        task.setDueDateTime(LocalDateTime.of(2019, 5, 19, 10, 0));
        task.setContext("old context");

        TaskPatch patch = new TaskPatch();
        patch.setId(UUID.randomUUID().toString());
        patch.setTaskId("10");
        patch.setDateTime(Instant.now());
        patch.addChange("status", "COMPLETED");
        patch.addChange("context", "new context");

        doReturn(Optional.of(task)).when(taskService).findById("10");
        doReturn(task).when(taskService).update(task);

        // act
        TaskPatchResult result = taskPatchService.patch(patch, Source.USER);

        // assert
        TaskPatchResult taskPatchResult = TaskPatchResult.builder()
                .task(task)
                .taskPatch(patch)
                .hasBeenCompleted(true)
                .build();

        assertThat(result, is(equalTo(taskPatchResult)));

        verify(taskService).findById("10");
        verify(eventPublisher).publishTaskPatched(patch);
        verify(eventPublisher).publishTaskCompleted(patch);
        verify(taskService).update(task);
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }

    @Test
    public void patchATaskThatHasBeenCompletedAndSourceIsEvent() {
        // arrange
        Task task = new Task();
        task.setId("10");
        task.setStatus(TaskStatus.OPEN);
        task.setDueDateTime(LocalDateTime.of(2019, 5, 19, 10, 0));
        task.setContext("old context");

        TaskPatch patch = new TaskPatch();
        patch.setId(UUID.randomUUID().toString());
        patch.setTaskId("10");
        patch.setDateTime(Instant.now());
        patch.addChange("status", "COMPLETED");
        patch.addChange("context", "new context");

        doReturn(Optional.of(task)).when(taskService).findById("10");
        doReturn(task).when(taskService).update(task);

        // act
        TaskPatchResult result = taskPatchService.patch(patch, Source.EVENT);

        // assert
        TaskPatchResult taskPatchResult = TaskPatchResult.builder()
                .task(task)
                .taskPatch(patch)
                .hasBeenCompleted(true)
                .build();

        assertThat(result, is(equalTo(taskPatchResult)));

        verify(taskService).findById("10");
        verify(taskService).update(task);
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }

    @Test
    public void patchATaskThatHasBeenUncompletedAndSourceIsUser() {
        // arrange
        Task task = new Task();
        task.setId("10");
        task.setStatus(TaskStatus.COMPLETED);
        task.setDueDateTime(LocalDateTime.of(2019, 5, 19, 10, 0));
        task.setContext("old context");

        TaskPatch patch = new TaskPatch();
        patch.setId(UUID.randomUUID().toString());
        patch.setTaskId("10");
        patch.setDateTime(Instant.now());
        patch.addChange("status", "OPEN");
        patch.addChange("context", "new context");

        doReturn(Optional.of(task)).when(taskService).findById("10");
        doReturn(task).when(taskService).update(task);

        // act
        TaskPatchResult result = taskPatchService.patch(patch, Source.USER);

        // assert
        TaskPatchResult taskPatchResult = TaskPatchResult.builder()
                .task(task)
                .taskPatch(patch)
                .hasBeenUncompleted(true)
                .build();

        assertThat(result, is(equalTo(taskPatchResult)));

        verify(taskService).findById("10");
        verify(eventPublisher).publishTaskPatched(patch);
        verify(eventPublisher).publishTaskRescheduled(patch);
        verify(taskService).update(task);
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }

    @Test
    public void patchATaskThatHasBeenUncompletedAndSourceIsEvent() {
        // arrange
        Task task = new Task();
        task.setId("10");
        task.setStatus(TaskStatus.COMPLETED);
        task.setDueDateTime(LocalDateTime.of(2019, 5, 19, 10, 0));
        task.setContext("old context");

        TaskPatch patch = new TaskPatch();
        patch.setId(UUID.randomUUID().toString());
        patch.setTaskId("10");
        patch.setDateTime(Instant.now());
        patch.addChange("status", "OPEN");
        patch.addChange("context", "new context");

        doReturn(Optional.of(task)).when(taskService).findById("10");
        doReturn(task).when(taskService).update(task);

        // act
        TaskPatchResult result = taskPatchService.patch(patch, Source.EVENT);

        // assert
        TaskPatchResult taskPatchResult = TaskPatchResult.builder()
                .task(task)
                .taskPatch(patch)
                .hasBeenUncompleted(true)
                .build();

        assertThat(result, is(equalTo(taskPatchResult)));

        verify(taskService).findById("10");
        verify(taskService).update(task);
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }

    @Test
    public void patchATaskThatHasBothHasBeenUncompletedAsWellAsRescheduledAndSourceIsUser() {
        // arrange
        Task task = new Task();
        task.setId("10");
        task.setStatus(TaskStatus.COMPLETED);
        task.setDueDateTime(LocalDateTime.of(2019, 5, 19, 10, 0));
        task.setContext("old context");

        TaskPatch patch = new TaskPatch();
        patch.setId(UUID.randomUUID().toString());
        patch.setTaskId("10");
        patch.setDateTime(Instant.now());
        patch.addChange("status", "OPEN");
        patch.addChange("dueDateTime", "2019-05-20T11:00:00");

        doReturn(Optional.of(task)).when(taskService).findById("10");
        doReturn(task).when(taskService).update(task);

        // act
        TaskPatchResult result = taskPatchService.patch(patch, Source.USER);

        // assert
        TaskPatchResult taskPatchResult = TaskPatchResult.builder()
                .task(task)
                .taskPatch(patch)
                .hasBeenUncompleted(true)
                .hasBeenRescheduled(true)
                .build();

        assertThat(result, is(equalTo(taskPatchResult)));

        verify(taskService).findById("10");
        verify(eventPublisher).publishTaskPatched(patch);
        verify(eventPublisher).publishTaskRescheduled(patch);
        verify(taskService).update(task);
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }

    @Test
    public void patchATaskThatHasBothHasBeenUncompletedAsWellAsRescheduledAndSourceIsEvent() {
        // arrange
        Task task = new Task();
        task.setId("10");
        task.setStatus(TaskStatus.COMPLETED);
        task.setDueDateTime(LocalDateTime.of(2019, 5, 19, 10, 0));
        task.setContext("old context");

        TaskPatch patch = new TaskPatch();
        patch.setId(UUID.randomUUID().toString());
        patch.setTaskId("10");
        patch.setDateTime(Instant.now());
        patch.addChange("status", "OPEN");
        patch.addChange("dueDateTime", "2019-05-20T11:00:00");

        doReturn(Optional.of(task)).when(taskService).findById("10");
        doReturn(task).when(taskService).update(task);

        // act
        TaskPatchResult result = taskPatchService.patch(patch, Source.EVENT);

        // assert
        TaskPatchResult taskPatchResult = TaskPatchResult.builder()
                .task(task)
                .taskPatch(patch)
                .hasBeenUncompleted(true)
                .hasBeenRescheduled(true)
                .build();

        assertThat(result, is(equalTo(taskPatchResult)));

        verify(taskService).findById("10");
        verify(taskService).update(task);
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }

    @Test
    public void patchATaskThatHasBeenRescheduledAndSourceIsUser() {
        // arrange
        Task task = new Task();
        task.setId("10");
        task.setStatus(TaskStatus.OPEN);
        task.setDueDateTime(LocalDateTime.of(2019, 5, 19, 10, 0));
        task.setContext("old context");

        TaskPatch patch = new TaskPatch();
        patch.setId(UUID.randomUUID().toString());
        patch.setTaskId("10");
        patch.setDateTime(Instant.now());
        patch.addChange("dueDateTime", "2019-05-20T11:00:00");

        doReturn(Optional.of(task)).when(taskService).findById("10");
        doReturn(task).when(taskService).update(task);

        // act
        TaskPatchResult result = taskPatchService.patch(patch, Source.USER);

        // assert
        TaskPatchResult taskPatchResult = TaskPatchResult.builder()
                .task(task)
                .taskPatch(patch)
                .hasBeenRescheduled(true)
                .build();

        assertThat(result, is(equalTo(taskPatchResult)));

        verify(taskService).findById("10");
        verify(eventPublisher).publishTaskPatched(patch);
        verify(eventPublisher).publishTaskRescheduled(patch);
        verify(taskService).update(task);
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }

    @Test
    public void patchATaskThatHasBeenRescheduledAndSourceIsEvent() {
        // arrange
        Task task = new Task();
        task.setId("10");
        task.setStatus(TaskStatus.OPEN);
        task.setDueDateTime(LocalDateTime.of(2019, 5, 19, 10, 0));
        task.setContext("old context");

        TaskPatch patch = new TaskPatch();
        patch.setId(UUID.randomUUID().toString());
        patch.setTaskId("10");
        patch.setDateTime(Instant.now());
        patch.addChange("dueDateTime", "2019-05-20T11:00:00");

        doReturn(Optional.of(task)).when(taskService).findById("10");
        doReturn(task).when(taskService).update(task);

        // act
        TaskPatchResult result = taskPatchService.patch(patch, Source.EVENT);

        // assert
        TaskPatchResult taskPatchResult = TaskPatchResult.builder()
                .task(task)
                .taskPatch(patch)
                .hasBeenRescheduled(true)
                .build();

        assertThat(result, is(equalTo(taskPatchResult)));

        verify(taskService).findById("10");
        verify(taskService).update(task);
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }

    @Test
    public void patchATaskThatHasBeenRescheduledAndCompletedAndSourceIsUser() {
        // arrange
        Task task = new Task();
        task.setId("10");
        task.setStatus(TaskStatus.OPEN);
        task.setDueDateTime(LocalDateTime.of(2019, 5, 19, 10, 0));
        task.setContext("old context");

        TaskPatch patch = new TaskPatch();
        patch.setId(UUID.randomUUID().toString());
        patch.setDateTime(Instant.now());
        patch.setTaskId("10");
        patch.setDateTime(Instant.now());
        patch.addChange("status", "COMPLETED");
        patch.addChange("dueDateTime", "2019-05-20T11:00:00");

        doReturn(Optional.of(task)).when(taskService).findById("10");
        doReturn(task).when(taskService).update(task);

        // act
        TaskPatchResult result = taskPatchService.patch(patch, Source.USER);

        // assert
        TaskPatchResult taskPatchResult = TaskPatchResult.builder()
                .task(task)
                .taskPatch(patch)
                .hasBeenCompleted(true)
                .hasBeenRescheduled(true)
                .build();

        assertThat(result, is(equalTo(taskPatchResult)));

        verify(taskService).findById("10");
        verify(eventPublisher).publishTaskPatched(patch);
        verify(eventPublisher).publishTaskRescheduled(patch);
        verify(eventPublisher).publishTaskCompleted(patch);
        verify(taskService).update(task);
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }

    @Test
    public void patchATaskThatHasBeenRescheduledAndCompletedAndSourceIsEvent() {
        // arrange
        Task task = new Task();
        task.setId("10");
        task.setStatus(TaskStatus.OPEN);
        task.setDueDateTime(LocalDateTime.of(2019, 5, 19, 10, 0));
        task.setContext("old context");

        TaskPatch patch = new TaskPatch();
        patch.setId(UUID.randomUUID().toString());
        patch.setDateTime(Instant.now());
        patch.setTaskId("10");
        patch.setDateTime(Instant.now());
        patch.addChange("status", "COMPLETED");
        patch.addChange("dueDateTime", "2019-05-20T11:00:00");

        doReturn(Optional.of(task)).when(taskService).findById("10");
        doReturn(task).when(taskService).update(task);

        // act
        TaskPatchResult result = taskPatchService.patch(patch, Source.EVENT);

        // assert
        TaskPatchResult taskPatchResult = TaskPatchResult.builder()
                .task(task)
                .taskPatch(patch)
                .hasBeenCompleted(true)
                .hasBeenRescheduled(true)
                .build();

        assertThat(result, is(equalTo(taskPatchResult)));

        verify(taskService).findById("10");
        verify(taskService).update(task);
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }

    @Test
    public void patchATaskThatHasNoChangesToStatusOrDueDateAndSourceIsUser() {
        // arrange
        Task task = new Task();
        task.setId("10");
        task.setStatus(TaskStatus.OPEN);
        task.setDueDateTime(LocalDateTime.of(2019, 5, 19, 10, 0));
        task.setContext("old context");

        TaskPatch patch = new TaskPatch();
        patch.setId(UUID.randomUUID().toString());
        patch.setTaskId("10");
        patch.setDateTime(Instant.now());
        patch.addChange("context", "new context");

        doReturn(Optional.of(task)).when(taskService).findById("10");
        doReturn(task).when(taskService).update(task);

        // act
        TaskPatchResult result = taskPatchService.patch(patch, Source.USER);

        // assert
        TaskPatchResult taskPatchResult = TaskPatchResult.builder()
                .task(task)
                .taskPatch(patch)
                .build();

        assertThat(result, is(equalTo(taskPatchResult)));

        verify(taskService).findById("10");
        verify(eventPublisher).publishTaskPatched(patch);
        verify(taskService).update(task);
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }

    @Test
    public void patchWhenTaskDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> {

            Task task = new Task();
            task.setId("10");

            TaskPatch patch = new TaskPatch();
            patch.setTaskId("10");
            patch.setDateTime(Instant.now());
            patch.addChange("status", "OPEN");
            patch.addChange("dueDateTime", "2019-05-20T11:00:00");

            doReturn(Optional.empty()).when(taskService).findById("10");

            taskPatchService.patch(patch, Source.EVENT);

            verify(taskService).findById("10");
            verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
        });
    }
}
