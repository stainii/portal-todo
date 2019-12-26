package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.dtos.TaskTemplateEntry;
import be.stijnhooft.portal.todo.mappers.TaskPatchMapper;
import be.stijnhooft.portal.todo.messaging.EventPublisher;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import be.stijnhooft.portal.todo.model.task.TaskStatus;
import be.stijnhooft.portal.todo.repositories.TaskPatchRepository;
import be.stijnhooft.portal.todo.repositories.TaskRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskPatchRepository taskPatchRepository;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private TaskTemplateService taskTemplateService;

    @Mock
    private TaskPatchMapper taskPatchMapper;

    private Clock clock = Clock.fixed(ZonedDateTime.of(2019, 11, 20, 10, 0, 0, 0, ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

    private TaskService taskService;

    @Before
    public void init() {
        taskService = new TaskService(taskRepository, taskPatchRepository, taskTemplateService, eventPublisher, taskPatchMapper, clock);
    }

    @Test
    public void createWhenNewTaskHasNoStatusYet() {
        // arrange
        LocalDateTime startDateTime = LocalDateTime.now();
        Task task = new Task();
        task.setStartDateTime(startDateTime);

        TaskPatch patch = new TaskPatch();

        doReturn(patch).when(taskPatchMapper).from(task);

        // act
        Task createdTask = taskService.create(task);

        // assert
        verify(taskRepository).save(task);
        verify(taskPatchMapper).from(task);
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
        task.setStatus(status);

        TaskPatch patch = new TaskPatch();

        doReturn(patch).when(taskPatchMapper).from(task);

        // act
        Task createdTask = taskService.create(task);

        // assert
        verify(taskRepository).save(task);
        verify(taskPatchMapper).from(task);
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
        task.setStatus(status);
        task.setStartDateTime(startDateTime);

        TaskPatch patch = new TaskPatch();

        doReturn(patch).when(taskPatchMapper).from(task);

        // act
        Task createdTask = taskService.create(task);

        // assert
        verify(taskRepository).save(task);
        verify(taskPatchMapper).from(task);
        verify(taskPatchRepository).save(patch);
        verify(eventPublisher).publishTaskCreated(patch);

        assertThat(createdTask.getStartDateTime(), is(equalTo(startDateTime)));
        assertThat(createdTask.getStatus(), is(equalTo(status)));
    }

    @Test
    public void createWhenNewTaskHasNoStatusAndNoStartDateYet() {
        // arrange
        Task task = new Task();
        TaskPatch patch = new TaskPatch();

        doReturn(patch).when(taskPatchMapper).from(task);

        // act
        Task createdTask = taskService.create(task);

        // assert
        verify(taskRepository).save(task);
        verify(taskPatchMapper).from(task);
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
        TaskPatch patch = new TaskPatch();

        doReturn(Collections.singletonList(task)).when(taskTemplateService).toTasks(taskTemplateEntry);
        doReturn(task).when(taskRepository).save(task);
        doReturn(patch).when(taskPatchMapper).from(task);

        // act
        taskService.createTasksBasedOn(taskTemplateEntry);

        // assert
        verify(taskTemplateService).toTasks(taskTemplateEntry);
        verify(eventPublisher).publishTaskCreated(patch);
        verify(taskRepository).save(task);
        verify(taskPatchMapper).from(task);
        verify(taskPatchRepository).save(patch);
        verifyNoMoreInteractions(taskTemplateService, taskRepository, taskPatchRepository, eventPublisher);
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
        Task result = taskService.save(task);

        // assert
        assertThat(result, is(task));

        verify(taskPatchRepository).saveAll(task.getHistory());
        verify(taskRepository).save(task);
        verifyNoMoreInteractions(taskRepository, taskPatchRepository);
    }

    @Test
    public void saveWhenTaskHasNoHistory() {
        // arrange
        Task task = new Task();
        task.setId("10");

        doReturn(task).when(taskRepository).save(task);

        // act
        Task result = taskService.save(task);

        // assert
        assertThat(result, is(task));

        verify(taskPatchRepository).saveAll(new ArrayList<>());
        verify(taskRepository).save(task);
        verifyNoMoreInteractions(taskRepository, taskPatchRepository);
    }

}
