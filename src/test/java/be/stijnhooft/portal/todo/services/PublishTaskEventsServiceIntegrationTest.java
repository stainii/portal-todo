package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.messaging.EventPublisher;
import be.stijnhooft.portal.todo.messaging.EventTopic;
import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.model.TaskPatch;
import be.stijnhooft.portal.todo.model.TaskStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/** This is an integration test, firing internal events
 * and checking whether the correct external events are published **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class PublishTaskEventsServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private EventPublisher eventPublisher;

    @MockBean
    private EventTopic eventTopic;

    @Mock
    private MessageChannel messageChannel;

    @Test
    public void onCreate() {
        // arrange
        LocalDateTime dueDateTime = LocalDateTime.of(2019, 5, 31, 10, 0);

        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setTaskId("10");
        taskPatch.addChange("name", "name");
        taskPatch.addChange("dueDateTime", dueDateTime.toString());
        taskPatch.addChange("status", "OPEN");

        Task task = new Task();
        task.setId("10");
        task.setName("name");
        task.setDueDateTime(dueDateTime);
        task.setStatus(TaskStatus.OPEN);
        task.getHistory().add(taskPatch);
        taskService.save(task);

        doReturn(messageChannel).when(eventTopic).eventTopic();

        // act: fire event, that should get picked up by the TaskEventPublisher
        eventPublisher.publishTaskCreated(taskPatch);

        // assert
        ArgumentCaptor<Message<Collection<Event>>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(eventTopic, atLeastOnce()).eventTopic();
        verify(messageChannel).send(messageCaptor.capture());

        Collection<Event> payload = messageCaptor.getValue().getPayload();
        assertEquals(1, payload.size());
        Event event = payload.iterator().next();
        assertEquals("Todo-10", event.getFlowId());
        assertEquals("Todo", event.getSource());
        assertNotNull(event.getPublishDate());
        assertEquals(3, event.getData().keySet().size());
        assertEquals("schedule", event.getData().get("type"));
        assertEquals("name", event.getData().get("task"));
        assertEquals(dueDateTime.toString(), event.getData().get("dueDate"));
    }

    @Test
    public void onReschedule() {
        // arrange
        LocalDateTime newDueDateTime = LocalDateTime.of(2019, 5, 31, 10, 0);

        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setTaskId("10");
        taskPatch.addChange("name", "name");
        taskPatch.addChange("dueDateTime", newDueDateTime.toString());

        Task task = new Task();
        task.setId("10");
        task.setName("name");
        task.setDueDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.patch(taskPatch);
        taskService.save(task);

        doReturn(messageChannel).when(eventTopic).eventTopic();

        // act: fire event, that should get picked up by the TaskEventPublisher
        eventPublisher.publishTaskRescheduled(taskPatch);

        // assert
        ArgumentCaptor<Message<Collection<Event>>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(eventTopic, atLeastOnce()).eventTopic();
        verify(messageChannel, times(2)).send(messageCaptor.capture());

        List<Message<Collection<Event>>> allCapturedPayloads = messageCaptor.getAllValues();
        assertEquals(2, allCapturedPayloads.size());

        Collection<Event> payload1 = allCapturedPayloads.get(0).getPayload();
        assertEquals(1, payload1.size());
        Event event1 = payload1.iterator().next();
        assertEquals("Todo-10", event1.getFlowId());
        assertEquals("Todo", event1.getSource());
        assertNotNull(event1.getPublishDate());
        assertEquals(1, event1.getData().keySet().size());
        assertEquals("cancellation", event1.getData().get("type"));

        Collection<Event> payload2 = allCapturedPayloads.get(1).getPayload();
        assertEquals(1, payload2.size());
        Event event2 = payload2.iterator().next();
        assertEquals("Todo-10", event2.getFlowId());
        assertEquals("Todo", event2.getSource());
        assertNotNull(event1.getPublishDate());
        assertEquals(3, event2.getData().keySet().size());
        assertEquals("schedule", event2.getData().get("type"));
        assertEquals("name", event2.getData().get("task"));
        assertEquals(newDueDateTime.toString(), event2.getData().get("dueDate"));
    }

    @Test
    public void onCompleted() {
        // data set
        LocalDateTime dueDateTime = LocalDateTime.of(2019, 5, 31, 10, 0);

        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setTaskId("10");
        taskPatch.addChange("name", "name");
        taskPatch.addChange("dueDateTime", dueDateTime.toString());
        taskPatch.addChange("status", "COMPLETED");

        // mock
        doReturn(messageChannel).when(eventTopic).eventTopic();

        // fire event, that should get picked up by the TaskEventPublisher
        eventPublisher.publishTaskCompleted(taskPatch);

        // assert and verify
        ArgumentCaptor<Message<Collection<Event>>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(eventTopic, atLeastOnce()).eventTopic();
        verify(messageChannel).send(messageCaptor.capture());

        Collection<Event> payload = messageCaptor.getValue().getPayload();
        assertEquals(1, payload.size());
        Event event = payload.iterator().next();
        assertEquals("Todo-10", event.getFlowId());
        assertEquals("Todo", event.getSource());
        assertNotNull(event.getPublishDate());
        assertEquals(1, event.getData().keySet().size());
        assertEquals("cancellation", event.getData().get("type"));
    }

    @Test
    public void onCancelled() {
        // data set
        LocalDateTime dueDateTime = LocalDateTime.of(2019, 5, 31, 10, 0);

        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setTaskId("10");
        taskPatch.addChange("name", "name");
        taskPatch.addChange("dueDateTime", dueDateTime.toString());
        taskPatch.addChange("status", "OPEN");

        // mock
        doReturn(messageChannel).when(eventTopic).eventTopic();

        // fire event, that should get picked up by the TaskEventPublisher
        eventPublisher.publishTaskCancelled(taskPatch);

        // assert and verify
        ArgumentCaptor<Message<Collection<Event>>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(eventTopic, atLeastOnce()).eventTopic();
        verify(messageChannel).send(messageCaptor.capture());

        Collection<Event> payload = messageCaptor.getValue().getPayload();
        assertEquals(1, payload.size());
        Event event = payload.iterator().next();
        assertEquals("Todo-10", event.getFlowId());
        assertEquals("Todo", event.getSource());
        assertNotNull(event.getPublishDate());
        assertEquals(1, event.getData().keySet().size());
        assertEquals("cancellation", event.getData().get("type"));
    }
}
