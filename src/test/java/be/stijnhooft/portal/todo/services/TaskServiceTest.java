package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.dtos.TaskTemplateEntry;
import be.stijnhooft.portal.todo.mappers.TaskPatchMapper;
import be.stijnhooft.portal.todo.messaging.EventPublisher;
import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.model.TaskPatch;
import be.stijnhooft.portal.todo.repositories.TaskPatchRepository;
import be.stijnhooft.portal.todo.repositories.TaskRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
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

    @InjectMocks
    private TaskService taskService;

    @Test
    public void createFromTemplateEntryWhenSuccess() {
        // arrange
        TaskTemplateEntry taskTemplateEntry = new TaskTemplateEntry();

        Task task = new Task();
        TaskPatch taskPatch = new TaskPatch();

        doReturn(task).when(taskTemplateService).toTask(taskTemplateEntry);
        doReturn(task).when(taskRepository).save(task);
        doReturn(taskPatch).when(taskPatchMapper).from(task);

        // act
        Task result = taskService.createTasksBasedOn(taskTemplateEntry);

        // assert
        verify(taskTemplateService).toTask(taskTemplateEntry);
        verify(eventPublisher).publishTaskCreated(taskPatch);
        verify(taskRepository).save(task);
        verify(taskPatchMapper).from(task);
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
