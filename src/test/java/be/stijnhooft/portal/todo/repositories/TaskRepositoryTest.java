package be.stijnhooft.portal.todo.repositories;

import be.stijnhooft.portal.todo.PortalTodoApplication;
import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.model.TaskStatus;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalTodoApplication.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DatabaseSetup("/datasets/TaskRepositoryTest-initial.xml")
    @DatabaseTearDown("/datasets/clear.xml")
    public void findAllByStatusWhenProvidingOneStatus() {
        List<Task> tasks = taskRepository.findAllByStatusIn(Collections.singletonList(TaskStatus.OPEN));
        assertThat(tasks.size(), is(2));
        assertThat(tasks.get(0).getId(), is(1L));
        assertThat(tasks.get(1).getId(), is(3L));
    }

    @Test
    @DatabaseSetup("/datasets/TaskRepositoryTest-initial.xml")
    @DatabaseTearDown("/datasets/clear.xml")
    public void findAllByStatusWhenProvidingMultipleStatuses() {
        List<Task> tasks = taskRepository.findAllByStatusIn(Arrays.asList(TaskStatus.OPEN, TaskStatus.CLOSED));
        assertThat(tasks.size(), is(3));
        assertThat(tasks.get(0).getId(), is(1L));
        assertThat(tasks.get(1).getId(), is(2L));
        assertThat(tasks.get(2).getId(), is(3L));
    }

    @Test
    @DatabaseSetup("/datasets/TaskRepositoryTest-initial.xml")
    @DatabaseTearDown("/datasets/clear.xml")
    public void findAllByStatusWhenProvidingEmptyList() {
        assertThat(taskRepository.findAllByStatusIn(new ArrayList<>()), is(empty()));
    }

}
