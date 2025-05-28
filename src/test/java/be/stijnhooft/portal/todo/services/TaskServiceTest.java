package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.dtos.TaskTemplateEntry;
import be.stijnhooft.portal.todo.mappers.TaskMapper;
import be.stijnhooft.portal.todo.mappers.TaskPatchMapper;
import be.stijnhooft.portal.todo.messaging.EventPublisher;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import be.stijnhooft.portal.todo.model.task.TaskStatus;
import be.stijnhooft.portal.todo.repositories.TaskPatchRepository;
import be.stijnhooft.portal.todo.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskPatchRepository taskPatchRepository;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private TaskPatchMapper taskPatchMapper;

    @Mock
    private TaskMapper taskMapper;

    private Clock clock = Clock.fixed(ZonedDateTime.of(2019, 11, 20, 10, 0, 0, 0, ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

    private TaskService taskService;

    @BeforeEach
    public void init() {
        taskService = new TaskService(taskRepository, taskPatchRepository, taskMapper, eventPublisher, taskPatchMapper, clock);
    }

    @Test
    public void createMultipleTasks() {
        // arrange
        var task1 = new Task();
        task1.setId(UUID.randomUUID().toString());

        var task2 = new Task();
        task2.setId(UUID.randomUUID().toString());

        var patch1 = new TaskPatch();
        patch1.setTaskId(task1.getId());
        patch1.setDateTime(Instant.now());

        var patch2 = new TaskPatch();
        patch2.setTaskId(task2.getId());
        patch2.setDateTime(Instant.now());

        doReturn(patch1).when(taskPatchMapper).mapToPatchThatCreatesATask(task1);
        doReturn(patch2).when(taskPatchMapper).mapToPatchThatCreatesATask(task2);

        // act
        var createdTasks = taskService.create(List.of(task1, task2));

        // assert
        verify(taskRepository).save(task1);
        verify(taskPatchMapper).mapToPatchThatCreatesATask(task1);
        verify(taskPatchRepository).save(patch1);
        verify(eventPublisher).publishTaskCreated(patch1);

        verify(taskRepository).save(task2);
        verify(taskPatchMapper).mapToPatchThatCreatesATask(task2);
        verify(taskPatchRepository).save(patch2);
        verify(eventPublisher).publishTaskCreated(patch2);

        assertThat(createdTasks.size(), is(2));
        assertThat(createdTasks.get(0).getStatus(), is(equalTo(TaskStatus.OPEN)));
        assertThat(createdTasks.get(1).getStatus(), is(equalTo(TaskStatus.OPEN)));
    }

    @Test
    public void createWhenNewTaskHasNoStatusYet() {
        // arrange
        LocalDateTime startDateTime = LocalDateTime.now();
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setStartDateTime(startDateTime);

        TaskPatch patch = new TaskPatch();
        patch.setDateTime(Instant.now());

        doReturn(patch).when(taskPatchMapper).mapToPatchThatCreatesATask(task);

        // act
        Task createdTask = taskService.create(task);

        // assert
        verify(taskRepository).save(task);
        verify(taskPatchMapper).mapToPatchThatCreatesATask(task);
        verify(taskPatchRepository).save(patch);
        verify(eventPublisher).publishTaskCreated(patch);

        assertThat(createdTask.getStartDateTime(), is(equalTo(startDateTime)));
        assertThat(createdTask.getStatus(), is(equalTo(TaskStatus.OPEN)));
    }

    @Test
    public void createWhenNewTaskHasNoStartDateYet() {
        // arrange
        TaskStatus status = TaskStatus.COMPLETED;
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setStatus(status);

        TaskPatch patch = new TaskPatch();
        patch.setDateTime(Instant.now());

        doReturn(patch).when(taskPatchMapper).mapToPatchThatCreatesATask(task);

        // act
        Task createdTask = taskService.create(task);

        // assert
        verify(taskRepository).save(task);
        verify(taskPatchMapper).mapToPatchThatCreatesATask(task);
        verify(taskPatchRepository).save(patch);
        verify(eventPublisher).publishTaskCreated(patch);

        assertThat(createdTask.getStartDateTime(), is(equalTo(LocalDateTime.of(2019, 11, 20, 10, 0, 0))));
        assertThat(createdTask.getStatus(), is(equalTo(status)));
    }

    @Test
    public void createWhenNewTaskHasAllRequiredFieldsFilledIn() {
        // arrange
        LocalDateTime startDateTime = LocalDateTime.now();
        TaskStatus status = TaskStatus.COMPLETED;
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setStatus(status);
        task.setStartDateTime(startDateTime);

        TaskPatch patch = new TaskPatch();
        patch.setDateTime(Instant.now());

        doReturn(patch).when(taskPatchMapper).mapToPatchThatCreatesATask(task);

        // act
        Task createdTask = taskService.create(task);

        // assert
        verify(taskRepository).save(task);
        verify(taskPatchMapper).mapToPatchThatCreatesATask(task);
        verify(taskPatchRepository).save(patch);
        verify(eventPublisher).publishTaskCreated(patch);

        assertThat(createdTask.getStartDateTime(), is(equalTo(startDateTime)));
        assertThat(createdTask.getStatus(), is(equalTo(status)));
    }

    @Test
    public void createWhenNewTaskWhenTheFLowIdIsFromThisApplication() {
        // arrange
        LocalDateTime startDateTime = LocalDateTime.now();
        TaskStatus status = TaskStatus.COMPLETED;

        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setStatus(status);
        task.setStartDateTime(startDateTime);
        task.setFlowId("Todo-100");

        TaskPatch patch = new TaskPatch();
        patch.setDateTime(Instant.now());

        doReturn(patch).when(taskPatchMapper).mapToPatchThatCreatesATask(task);

        // act
        Task createdTask = taskService.create(task);

        // assert
        verify(taskRepository).save(task);
        verify(taskPatchMapper).mapToPatchThatCreatesATask(task);
        verify(taskPatchRepository).save(patch);
        verify(eventPublisher).publishTaskCreated(patch);

        assertThat(createdTask.getStartDateTime(), is(equalTo(startDateTime)));
        assertThat(createdTask.getStatus(), is(equalTo(status)));
    }

    @Test
    public void createWhenNewTaskWhenTheFLowIdIsFromAnotherApplication() {
        // arrange
        LocalDateTime startDateTime = LocalDateTime.now();
        TaskStatus status = TaskStatus.COMPLETED;

        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setStatus(status);
        task.setStartDateTime(startDateTime);
        task.setFlowId("Housagotchi-100");

        TaskPatch patch = new TaskPatch();
        patch.setDateTime(Instant.now());

        doReturn(patch).when(taskPatchMapper).mapToPatchThatCreatesATask(task);

        // act
        Task createdTask = taskService.create(task);

        // assert
        verify(taskRepository).save(task);
        verify(taskPatchMapper).mapToPatchThatCreatesATask(task);
        verify(taskPatchRepository).save(patch);
        verify(eventPublisher, times(0)).publishTaskCreated(patch);

        assertThat(createdTask.getStartDateTime(), is(equalTo(startDateTime)));
        assertThat(createdTask.getStatus(), is(equalTo(status)));
    }

    @Test
    public void createWhenNewTaskHasNoStatusAndNoStartDateYet() {
        // arrange
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());

        TaskPatch patch = new TaskPatch();
        patch.setDateTime(Instant.now());

        doReturn(patch).when(taskPatchMapper).mapToPatchThatCreatesATask(task);

        // act
        Task createdTask = taskService.create(task);

        // assert
        verify(taskRepository).save(task);
        verify(taskPatchMapper).mapToPatchThatCreatesATask(task);
        verify(taskPatchRepository).save(patch);
        verify(eventPublisher).publishTaskCreated(patch);

        assertThat(createdTask.getStartDateTime(), is(equalTo(LocalDateTime.of(2019, 11, 20, 10, 0, 0, 0))));
        assertThat(createdTask.getStatus(), is(equalTo(TaskStatus.OPEN)));
    }

    @Test
    public void createFromTemplateEntryWhenSuccess() {
        // arrange
        TaskTemplateEntry taskTemplateEntry = new TaskTemplateEntry();

        Task task = new Task();
        task.setId(UUID.randomUUID().toString());

        TaskPatch patch = new TaskPatch();
        patch.setDateTime(Instant.now());

        doReturn(Collections.singletonList(task)).when(taskMapper).mapToNewTask(taskTemplateEntry);
        doReturn(task).when(taskRepository).save(task);
        doReturn(patch).when(taskPatchMapper).mapToPatchThatCreatesATask(task);

        // act
        taskService.createTasksBasedOn(taskTemplateEntry);

        // assert
        verify(taskMapper).mapToNewTask(taskTemplateEntry);
        verify(eventPublisher).publishTaskCreated(patch);
        verify(taskRepository).save(task);
        verify(taskPatchMapper).mapToPatchThatCreatesATask(task);
        verify(taskPatchRepository).save(patch);
        verifyNoMoreInteractions(taskMapper, taskRepository, taskPatchRepository, eventPublisher);
    }

    @Test
    public void saveWhenTaskHasAHistory() {
        // arrange
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setTaskId("100");

        Task task = new Task();
        task.setId("10");
        task.getHistory().add(taskPatch);

        doReturn(task).when(taskRepository).save(task);

        // act
        Task result = taskService.update(task);

        // assert
        assertThat(result, is(task));

        verify(taskPatchRepository).saveAll(task.getHistory());
        verify(taskRepository).save(task);
        verifyNoMoreInteractions(taskMapper, taskRepository, taskPatchRepository);
    }

    @Test
    public void saveWhenTaskHasNoHistory() {
        // arrange
        Task task = new Task();
        task.setId("10");

        doReturn(task).when(taskRepository).save(task);

        // act
        Task result = taskService.update(task);

        // assert
        assertThat(result, is(task));

        verify(taskPatchRepository).saveAll(new ArrayList<>());
        verify(taskRepository).save(task);
        verifyNoMoreInteractions(taskMapper, taskRepository, taskPatchRepository);
    }

}
