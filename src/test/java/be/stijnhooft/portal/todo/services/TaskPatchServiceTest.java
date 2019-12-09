package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.messaging.EventPublisher;
import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.model.TaskPatch;
import be.stijnhooft.portal.todo.model.TaskPatchResult;
import be.stijnhooft.portal.todo.model.TaskStatus;
import be.stijnhooft.portal.todo.repositories.TaskPatchRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskPatchServiceTest {

    @Mock
    private TaskPatchRepository taskPatchRepository;

    @Mock
    private TaskService taskService;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private TaskPatchService taskPatchService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void patchATaskThatHasBeenCompleted() {
        // arrange
        Task task = new Task();
        task.setId("10");
        task.setStatus(TaskStatus.OPEN);
        task.setDueDateTime(LocalDateTime.of(2019, 5, 19, 10, 0));
        task.setContext("old context");

        TaskPatch patch = new TaskPatch();
        patch.setTaskId("10");
        patch.addChange("status", "COMPLETED");
        patch.addChange("context", "new context");

        doReturn(Optional.of(task)).when(taskService).findById("10");
        doReturn(task).when(taskService).save(task);

        // act
        TaskPatchResult result = taskPatchService.patch(patch);

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
        verify(taskService).save(task);
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }

    @Test
    public void patchATaskThatHasBeenUncompleted() {
        // arrange
        Task task = new Task();
        task.setId("10");
        task.setStatus(TaskStatus.COMPLETED);
        task.setDueDateTime(LocalDateTime.of(2019, 5, 19, 10, 0));
        task.setContext("old context");

        TaskPatch patch = new TaskPatch();
        patch.setTaskId("10");
        patch.addChange("status", "OPEN");
        patch.addChange("context", "new context");

        doReturn(Optional.of(task)).when(taskService).findById("10");
        doReturn(task).when(taskService).save(task);

        // act
        TaskPatchResult result = taskPatchService.patch(patch);

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
        verify(taskService).save(task);
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }

    @Test
    public void patchATaskThatHasBothHasBeenUncompletedAsWellAsRescheduled() {
        // arrange
        Task task = new Task();
        task.setId("10");
        task.setStatus(TaskStatus.COMPLETED);
        task.setDueDateTime(LocalDateTime.of(2019, 5, 19, 10, 0));
        task.setContext("old context");

        TaskPatch patch = new TaskPatch();
        patch.setTaskId("10");
        patch.addChange("status", "OPEN");
        patch.addChange("dueDateTime", "2019-05-20T11:00:00Z");

        doReturn(Optional.of(task)).when(taskService).findById("10");
        doReturn(task).when(taskService).save(task);

        // act
        TaskPatchResult result = taskPatchService.patch(patch);

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
        verify(taskService).save(task);
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }

    @Test
    public void patchATaskThatHasBeenRescheduled() {
        // arrange
        Task task = new Task();
        task.setId("10");
        task.setStatus(TaskStatus.OPEN);
        task.setDueDateTime(LocalDateTime.of(2019, 5, 19, 10, 0));
        task.setContext("old context");

        TaskPatch patch = new TaskPatch();
        patch.setTaskId("10");
        patch.addChange("dueDateTime", "2019-05-20T11:00:00Z");

        doReturn(Optional.of(task)).when(taskService).findById("10");
        doReturn(task).when(taskService).save(task);

        // act
        TaskPatchResult result = taskPatchService.patch(patch);

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
        verify(taskService).save(task);
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }

    @Test
    public void patchATaskThatHasBeenRescheduledAndCompleted() {
        // arrange
        Task task = new Task();
        task.setId("10");
        task.setStatus(TaskStatus.OPEN);
        task.setDueDateTime(LocalDateTime.of(2019, 5, 19, 10, 0));
        task.setContext("old context");

        TaskPatch patch = new TaskPatch();
        patch.setTaskId("10");
        patch.addChange("status", "COMPLETED");
        patch.addChange("dueDateTime", "2019-05-20T11:00:00Z");

        doReturn(Optional.of(task)).when(taskService).findById("10");
        doReturn(task).when(taskService).save(task);

        // act
        TaskPatchResult result = taskPatchService.patch(patch);

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
        verify(taskService).save(task);
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }

    @Test
    public void patchATaskThatHasNoChangesToStatusOrDueDate() {
        // arrange
        Task task = new Task();
        task.setId("10");
        task.setStatus(TaskStatus.OPEN);
        task.setDueDateTime(LocalDateTime.of(2019, 5, 19, 10, 0));
        task.setContext("old context");

        TaskPatch patch = new TaskPatch();
        patch.setTaskId("10");
        patch.addChange("context", "new context");

        doReturn(Optional.of(task)).when(taskService).findById("10");
        doReturn(task).when(taskService).save(task);

        // act
        TaskPatchResult result = taskPatchService.patch(patch);

        // assert
        TaskPatchResult taskPatchResult = TaskPatchResult.builder()
                .task(task)
                .taskPatch(patch)
                .build();

        assertThat(result, is(equalTo(taskPatchResult)));

        verify(taskService).findById("10");
        verify(eventPublisher).publishTaskPatched(patch);
        verify(taskService).save(task);
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }

    @Test
    public void patchWhenTaskDoesNotExist() {
        expectedException.expect(IllegalArgumentException.class);

        Task task = new Task();
        task.setId("10");

        TaskPatch patch = new TaskPatch();
        patch.setTaskId("10");
        patch.addChange("status", "OPEN");
        patch.addChange("dueDateTime", "2019-05-20T11:00:00");

        doReturn(Optional.empty()).when(taskService).findById("10");

        taskPatchService.patch(patch);

        verify(taskService).findById("10");
        verifyNoMoreInteractions(taskService, taskPatchRepository, eventPublisher);
    }


}
