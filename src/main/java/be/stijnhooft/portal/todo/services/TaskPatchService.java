package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.messaging.EventPublisher;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import be.stijnhooft.portal.todo.model.task.TaskPatchResult;
import be.stijnhooft.portal.todo.repositories.TaskPatchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@Transactional
public class TaskPatchService {

    private final TaskService taskService;
    private final EventPublisher eventPublisher;
    private final TaskPatchRepository taskPatchRepository;

    @Autowired
    public TaskPatchService(TaskService taskService, EventPublisher eventPublisher, TaskPatchRepository taskPatchRepository) {
        this.taskService = taskService;
        this.eventPublisher = eventPublisher;
        this.taskPatchRepository = taskPatchRepository;
    }

    public List<TaskPatch> findAllTaskPatchesSince(Instant startDateTime) {
        return taskPatchRepository.findByDateTimeAfter(startDateTime);
    }

    public TaskPatchResult patch(TaskPatch taskPatch) {
        Task task = taskService.findById(taskPatch.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find task with id " + taskPatch.getTaskId()));

        TaskPatchResult patchResult = task.patch(taskPatch);

        taskService.save(task);

        publishTaskPatchedEvent(taskPatch);
        publishEventWhenTaskHasBeenRescheduled(patchResult);
        publishEventIfTaskHasBeenCompleted(patchResult);

        return patchResult;
    }

    private void publishTaskPatchedEvent(TaskPatch taskPatch) {
        eventPublisher.publishTaskPatched(taskPatch);
    }

    private void publishEventIfTaskHasBeenCompleted(TaskPatchResult taskPatchResult) {
        if (taskPatchResult.hasBeenCompleted()) {
            eventPublisher.publishTaskCompleted(taskPatchResult.getTaskPatch());
        }
    }

    private void publishEventWhenTaskHasBeenRescheduled(TaskPatchResult taskPatchResult) {
        if (taskPatchResult.hasBeenUncompleted()
                || taskPatchResult.hasBeenRescheduled()) {
            eventPublisher.publishTaskRescheduled(taskPatchResult.getTaskPatch());
        }
    }

}
