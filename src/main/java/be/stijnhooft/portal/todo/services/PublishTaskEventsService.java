package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.events.TaskCancelled;
import be.stijnhooft.portal.todo.events.TaskCompleted;
import be.stijnhooft.portal.todo.events.TaskCreated;
import be.stijnhooft.portal.todo.events.TaskRescheduled;
import be.stijnhooft.portal.todo.mappers.CancellationEventMapper;
import be.stijnhooft.portal.todo.mappers.ScheduleEventMapper;
import be.stijnhooft.portal.todo.messaging.EventPublisher;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import static be.stijnhooft.portal.todo.PortalTodoApplication.APPLICATION_NAME;

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
    public void onCreate(TaskCreated taskCreated) {
        scheduleEvent(taskCreated.getTaskPatch());
    }

    @EventListener
    public void onReschedule(TaskRescheduled taskRescheduled) {
        if (taskRescheduled.getTaskPatch().getFlowId().startsWith(APPLICATION_NAME)) {
            cancelEvent(taskRescheduled.getTaskPatch());
            scheduleEvent(taskRescheduled.getTaskPatch());
        }
    }

    @EventListener
    public void onCompleted(TaskCompleted taskCompleted) {
        cancelEvent(taskCompleted.getTaskPatch());
    }

    @EventListener
    public void onCancelled(TaskCancelled taskCancelled) {
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
