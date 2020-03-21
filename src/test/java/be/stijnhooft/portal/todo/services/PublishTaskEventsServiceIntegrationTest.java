package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.messaging.EventPublisher;
import be.stijnhooft.portal.todo.messaging.EventTopic;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import be.stijnhooft.portal.todo.model.task.TaskStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import static be.stijnhooft.portal.todo.PortalTodoApplication.APPLICATION_NAME;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;

/** This is an integration test, firing internal events
 * and checking whether the correct external events are published **/
@SuppressWarnings("unchecked")
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class PublishTaskEventsServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private EventTopic eventTopic;

    @Autowired
    private MessageCollector collector;

    @SuppressWarnings("unchecked")
    @Test
    public void onCreate() {
        // arrange
        var dueDateTime = ZonedDateTime.of(2019, 5, 31, 10, 0, 0, 0, ZoneId.of("Europe/Brussels")).toInstant();

        var taskPatch = new TaskPatch();
        taskPatch.setTaskId("10");
        taskPatch.setFlowId(APPLICATION_NAME + "-10");
        taskPatch.addChange("name", "name");
        taskPatch.addChange("dueDateTime", dueDateTime.toString());
        taskPatch.addChange("status", "OPEN");

        var task = new Task();
        task.setId("10");
        task.setFlowId(APPLICATION_NAME + "-10");
        task.setName("name");
        task.setDueDateTime(LocalDateTime.of(2019, 5, 31, 10, 0));
        task.setStatus(TaskStatus.OPEN);
        task.getHistory().add(taskPatch);
        taskService.update(task);

        // act: fire event, that should get picked up by the TaskEventPublisher
        eventPublisher.publishTaskCreated(taskPatch);

        // assert
        BlockingQueue<Message<?>> messages = collector.forChannel(eventTopic.writeToEventTopic());

        assertThat(messages, receivesPayloadThat(allOf(
                containsString("\"source\":\"Todo\""),
                containsString("\"flowId\":\"Todo-10\""),
                containsString("\"data\":{\"task\":\"name\",\"dueDate\":\"2019-05-31T10:00\",\"type\":\"schedule\"}"))));
    }

    @Test
    public void onReschedule() {
        // arrange
        var taskPatch = new TaskPatch();
        taskPatch.setId(UUID.randomUUID().toString());
        taskPatch.setTaskId("10");
        taskPatch.setFlowId(APPLICATION_NAME + "-10");
        taskPatch.setDateTime(Instant.now());
        taskPatch.addChange("name", "name");
        taskPatch.addChange("dueDateTime", "2019-05-31T10:00:00");

        var task = new Task();
        task.setId("10");
        task.setFlowId(APPLICATION_NAME + "-10");
        task.setName("name");
        task.setDueDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.patch(taskPatch);
        taskService.update(task);

        // act: fire event, that should get picked up by the TaskEventPublisher
        eventPublisher.publishTaskRescheduled(taskPatch);

        // assert
        BlockingQueue<Message<?>> messages = collector.forChannel(eventTopic.writeToEventTopic());

        assertThat(messages, receivesPayloadThat(allOf(
                containsString("\"source\":\"Todo\""),
                containsString("\"flowId\":\"Todo-10\""),
                containsString("\"data\":{\"type\":\"cancellation\"}"))));
        assertThat(messages, receivesPayloadThat(allOf(
                containsString("\"source\":\"Todo\""),
                containsString("\"flowId\":\"Todo-10\""),
                containsString("\"data\":{\"task\":\"name\",\"dueDate\":\"2019-05-31T10:00\",\"type\":\"schedule\"}"))));
    }

    @Test
    public void onCompleted() {
        // data set
        var dueDateTime = ZonedDateTime.of(2019, 5, 31, 10, 0, 0, 0, ZoneId.of("Europe/Brussels")).toInstant();

        var taskPatch = new TaskPatch();
        taskPatch.setTaskId("10");
        taskPatch.setFlowId(APPLICATION_NAME + "-10");
        taskPatch.addChange("name", "name");
        taskPatch.addChange("dueDateTime", dueDateTime.toString());
        taskPatch.addChange("status", "COMPLETED");

        // act: fire event, that should get picked up by the TaskEventPublisher
        eventPublisher.publishTaskCompleted(taskPatch);

        // assert and verify
        BlockingQueue<Message<?>> messages = collector.forChannel(eventTopic.writeToEventTopic());

        assertThat(messages, receivesPayloadThat(allOf(
                containsString("\"source\":\"Todo\""),
                containsString("\"flowId\":\"Todo-10\""),
                containsString("\"data\":{\"type\":\"cancellation\"}"))));
    }

    @Test
    public void onCancelled() {
        // data set
        var dueDateTime = ZonedDateTime.of(2019, 5, 31, 10, 0, 0, 0, ZoneId.of("Europe/Brussels")).toInstant();

        var taskPatch = new TaskPatch();
        taskPatch.setTaskId("10");
        taskPatch.setFlowId(APPLICATION_NAME + "-10");
        taskPatch.addChange("name", "name");
        taskPatch.addChange("dueDateTime", dueDateTime.toString());
        taskPatch.addChange("status", "OPEN");

        // act: fire event, that should get picked up by the TaskEventPublisher
        eventPublisher.publishTaskCancelled(taskPatch);

        // assert and verify
        BlockingQueue<Message<?>> messages = collector.forChannel(eventTopic.writeToEventTopic());

        assertThat(messages, receivesPayloadThat(allOf(
                containsString("\"source\":\"Todo\""),
                containsString("\"flowId\":\"Todo-10\""),
                containsString("\"data\":{\"type\":\"cancellation\"}"))));
    }
}
