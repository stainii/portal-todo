package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.repositories.TaskRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;


    @Test
    public void updateWhenSuccess() {
        Task task = new Task();
        task.setId(10L);

        doReturn(Optional.of(task)).when(taskRepository).findById(10L);

        taskService.update(task);

        verify(taskRepository).findById(10L);
        verify(taskRepository).saveAndFlush(task);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateWhenTaskDoesNotExist() {
        Task task = new Task();
        task.setId(10L);

        doReturn(Optional.empty()).when(taskRepository).findById(10L);

        taskService.update(task);
    }

    @Test
    public void deleteWhenSuccess() {
        Task task = new Task();
        task.setId(10L);

        doReturn(Optional.of(task)).when(taskRepository).findById(10L);

        taskService.delete(10L);

        verify(taskRepository).findById(10L);
        verify(taskRepository).deleteById(10L);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteWhenTaskDoesNotExist() {
        doReturn(Optional.empty()).when(taskRepository).findById(10L);
        taskService.delete(10L);
    }

}
