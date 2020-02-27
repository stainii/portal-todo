package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.messaging.EventPublisher;
import be.stijnhooft.portal.todo.messaging.EventTopic;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import be.stijnhooft.portal.todo.model.task.TaskStatus;
import org.junit.Ignore;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/** This is an integration test, firing internal events
 * and checking whether the correct external events are published **/
@SuppressWarnings("unchecked")
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
@Ignore // TODO
public class PublishTaskEventsServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private EventPublisher eventPublisher;

    @MockBean
    private EventTopic eventTopic;

    @Mock
    private MessageChannel messageChannel;

    @SuppressWarnings("unchecked")
    @Test
    public void onCreate() {
        // arrange
        Instant dueDateTime = ZonedDateTime.of(2019, 5, 31, 10, 0, 0, 0, ZoneId.of("Europe/Brussels")).toInstant();

        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setTaskId("10");
        taskPatch.addChange("name", "name");
        taskPatch.addChange("dueDateTime", dueDateTime.toString());
        taskPatch.addChange("status", "OPEN");

        Task task = new Task();
        task.setId("10");
        task.setName("name");
        task.setDueDateTime(LocalDateTime.of(2019, 5, 31, 10, 0));
        task.setStatus(TaskStatus.OPEN);
        task.getHistory().add(taskPatch);
        taskService.save(task);

        doReturn(messageChannel).when(eventTopic).writeToEventTopic();

        // act: fire event, that should get picked up by the TaskEventPublisher
        eventPublisher.publishTaskCreated(taskPatch);

        // assert
        ArgumentCaptor<Message<Collection<Event>>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(eventTopic, atLeastOnce()).writeToEventTopic();
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
        assertEquals("2019-05-31T10:00", event.getData().get("dueDate"));
    }

    @Test
    public void onReschedule() {
        // arrange
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setId(UUID.randomUUID().toString());
        taskPatch.setTaskId("10");
        taskPatch.setDateTime(Instant.now());
        taskPatch.addChange("name", "name");
        taskPatch.addChange("dueDateTime", "2019-05-31T10:00:00");

        Task task = new Task();
        task.setId("10");
        task.setName("name");
        task.setDueDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.patch(taskPatch);
        taskService.save(task);

        doReturn(messageChannel).when(eventTopic).writeToEventTopic();

        // act: fire event, that should get picked up by the TaskEventPublisher
        eventPublisher.publishTaskRescheduled(taskPatch);

        // assert
        ArgumentCaptor<Message<Collection<Event>>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(eventTopic, atLeastOnce()).writeToEventTopic();
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
        assertEquals("2019-05-31T10:00", event2.getData().get("dueDate"));
    }

    @Test
    public void onCompleted() {
        // data set
        Instant dueDateTime = ZonedDateTime.of(2019, 5, 31, 10, 0, 0, 0, ZoneId.of("Europe/Brussels")).toInstant();

        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setTaskId("10");
        taskPatch.addChange("name", "name");
        taskPatch.addChange("dueDateTime", dueDateTime.toString());
        taskPatch.addChange("status", "COMPLETED");

        // mock
        doReturn(messageChannel).when(eventTopic).writeToEventTopic();

        // fire event, that should get picked up by the TaskEventPublisher
        eventPublisher.publishTaskCompleted(taskPatch);

        // assert and verify
        ArgumentCaptor<Message<Collection<Event>>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(eventTopic, atLeastOnce()).writeToEventTopic();
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
        Instant dueDateTime = ZonedDateTime.of(2019, 5, 31, 10, 0, 0, 0, ZoneId.of("Europe/Brussels")).toInstant();

        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setTaskId("10");
        taskPatch.addChange("name", "name");
        taskPatch.addChange("dueDateTime", dueDateTime.toString());
        taskPatch.addChange("status", "OPEN");

        // mock
        doReturn(messageChannel).when(eventTopic).writeToEventTopic();

        // fire event, that should get picked up by the TaskEventPublisher
        eventPublisher.publishTaskCancelled(taskPatch);

        // assert and verify
        ArgumentCaptor<Message<Collection<Event>>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(eventTopic, atLeastOnce()).writeToEventTopic();
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
