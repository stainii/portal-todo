package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.events.TaskCancelled;
import be.stijnhooft.portal.todo.events.TaskCompleted;
import be.stijnhooft.portal.todo.events.TaskCreated;
import be.stijnhooft.portal.todo.events.TaskRescheduled;
import be.stijnhooft.portal.todo.mappers.CancellationEventMapper;
import be.stijnhooft.portal.todo.mappers.ScheduleEventMapper;
import be.stijnhooft.portal.todo.messaging.EventPublisher;
import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.model.TaskPatch;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class PublishTaskEventsService {

    private final CancellationEventMapper cancellationEventMapper;
    private final ScheduleEventMapper scheduleEventMapper;
    private final EventPublisher eventPublisher;

    PublishTaskEventsService(CancellationEventMapper cancellationEventMapper, ScheduleEventMapper scheduleEventMapper, EventPublisher eventPublisher) {
        this.cancellationEventMapper = cancellationEventMapper;
        this.scheduleEventMapper = scheduleEventMapper;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    void onCreate(TaskCreated taskCreated) {
        scheduleEvent(taskCreated.getTaskPatch());
    }

    @EventListener
    void onReschedule(TaskRescheduled taskRescheduled) {
        cancelEvent(taskRescheduled.getTaskPatch());
        scheduleEvent(taskRescheduled.getTaskPatch());
    }

    @EventListener
    void onCompleted(TaskCompleted taskCompleted) {
        cancelEvent(taskCompleted.getTaskPatch());
    }

    @EventListener
    void onCancelled(TaskCancelled taskCancelled) {
        cancelEvent(taskCancelled.getTaskPatch());
    }

    private void cancelEvent(TaskPatch taskPatch) {
        Event event = cancellationEventMapper.map(taskPatch);
        eventPublisher.publishEvent(event);
    }

    private void scheduleEvent(TaskPatch taskPatch) {
        Event event = scheduleEventMapper.map(taskPatch);
        eventPublisher.publishEvent(event);
    }
}
