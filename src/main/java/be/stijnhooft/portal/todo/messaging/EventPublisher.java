package be.stijnhooft.portal.todo.messaging;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.events.*;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
@EnableBinding(EventTopic.class)
@Slf4j
public class EventPublisher {

    private final EventTopic eventTopic;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public EventPublisher(EventTopic eventTopic, ApplicationEventPublisher applicationEventPublisher) {
        this.eventTopic = eventTopic;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishEvent(Event event) {
        publishEvent(Collections.singleton(event));
    }

    public void publishEvent(Collection<Event> events) {
        log.info("Sending events to the Event topic");
        log.debug(events.toString());
        eventTopic.writeToEventTopic().send(MessageBuilder.withPayload(events).build());
    }

    public void publishTaskCompleted(TaskPatch taskPatch) {
        applicationEventPublisher.publishEvent(new TaskCompleted(taskPatch));
    }

    public void publishTaskRescheduled(TaskPatch taskPatch) {
        applicationEventPublisher.publishEvent(new TaskRescheduled(taskPatch));
    }

    public void publishTaskCancelled(TaskPatch taskPatch) {
        applicationEventPublisher.publishEvent(new TaskCancelled(taskPatch));
    }

    public void publishTaskCreated(TaskPatch taskPatch) {
        applicationEventPublisher.publishEvent(new TaskCreated(taskPatch));
    }

    public void publishTaskPatched(TaskPatch taskPatch) {
        applicationEventPublisher.publishEvent(new TaskPatched(taskPatch));
    }
}
