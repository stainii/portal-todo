package be.stijnhooft.portal.todo.messaging;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.events.TaskCompleted;
import be.stijnhooft.portal.todo.events.TaskCreated;
import be.stijnhooft.portal.todo.events.TaskPatched;
import be.stijnhooft.portal.todo.events.TaskRescheduled;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventPublisher {

    private final StreamBridge streamBridge;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(Event event) {
        publishEvent(Collections.singleton(event));
    }

    public void publishEvent(Collection<Event> events) {
        log.info("Sending events to the Event topic");
        log.debug("{}", events);
        streamBridge.send("eventChannel-out-0", events);
    }

    public void publishTaskCompleted(TaskPatch taskPatch) {
        applicationEventPublisher.publishEvent(new TaskCompleted(taskPatch));
    }

    public void publishTaskRescheduled(TaskPatch taskPatch) {
        applicationEventPublisher.publishEvent(new TaskRescheduled(taskPatch));
    }

    public void publishTaskCreated(TaskPatch taskPatch) {
        applicationEventPublisher.publishEvent(new TaskCreated(taskPatch));
    }

    public void publishTaskPatched(TaskPatch taskPatch) {
        applicationEventPublisher.publishEvent(new TaskPatched(taskPatch));
    }
}
