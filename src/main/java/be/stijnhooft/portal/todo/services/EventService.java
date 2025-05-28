package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.dtos.FiringSubscription;
import be.stijnhooft.portal.todo.dtos.Source;
import be.stijnhooft.portal.todo.mappers.TaskMapper;
import be.stijnhooft.portal.todo.mappers.TaskPatchMapper;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class EventService {

    private final SubscriptionService subscriptionService;
    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final TaskPatchService taskPatchService;
    private final TaskPatchMapper taskPatchMapper;

    public EventService(SubscriptionService subscriptionService, TaskMapper taskMapper, TaskService taskService, TaskPatchService taskPatchService, TaskPatchMapper taskPatchMapper) {
        this.subscriptionService = subscriptionService;
        this.taskService = taskService;
        this.taskMapper = taskMapper;
        this.taskPatchService = taskPatchService;
        this.taskPatchMapper = taskPatchMapper;
    }

    public void receiveEvents(Collection<Event> events) {
        createNewTasks(events);
        completeTasks(events);
    }

    private void completeTasks(Collection<Event> events) {
        List<TaskPatch> taskPatches = events.parallelStream()
                .flatMap(subscriptionService::fireOnCompleteCondition)
                .map(FiringSubscription::getEvent)
                .flatMap(event -> taskPatchMapper.mapToTaskPatchThatCompletesATask(event).stream())
                .collect(Collectors.toList());

        if (taskPatches.isEmpty()) {
            log.info("Received events, but no cancellations were triggered.");
        } else {
            taskPatches.forEach(taskPatch -> taskPatchService.patch(taskPatch, Source.EVENT));
        }
    }

    private void createNewTasks(Collection<Event> events) {
        List<Task> tasks = events.parallelStream()
                .flatMap(subscriptionService::fireOnCreationCondition)
                .map(taskMapper::mapToNewTask)
                .collect(Collectors.toList());

        if (tasks.isEmpty()) {
            log.info("Received events, but no activations were triggered.");
        } else {
            taskService.create(tasks);
        }
    }

}
