package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.dtos.TaskTemplateEntry;
import be.stijnhooft.portal.todo.mappers.TaskMapper;
import be.stijnhooft.portal.todo.mappers.TaskPatchMapper;
import be.stijnhooft.portal.todo.messaging.EventPublisher;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import be.stijnhooft.portal.todo.model.task.TaskStatus;
import be.stijnhooft.portal.todo.repositories.TaskPatchRepository;
import be.stijnhooft.portal.todo.repositories.TaskRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static be.stijnhooft.portal.todo.PortalTodoApplication.APPLICATION_NAME;

@Service
@Slf4j
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskPatchRepository taskPatchRepository;
    private final TaskMapper taskMapper;
    private final EventPublisher eventPublisher;
    private final TaskPatchMapper taskPatchMapper;
    private final Clock clock;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskPatchRepository taskPatchRepository, TaskMapper taskMapper, EventPublisher eventPublisher, TaskPatchMapper taskPatchMapper, Clock clock) {
        this.taskRepository = taskRepository;
        this.taskPatchRepository = taskPatchRepository;
        this.taskMapper = taskMapper;
        this.eventPublisher = eventPublisher;
        this.taskPatchMapper = taskPatchMapper;
        this.clock = clock;
    }

    public List<Task> findAllActiveTasks() {
        LocalDateTime now = LocalDateTime.ofInstant(clock.instant(), ZoneId.systemDefault());
        return taskRepository.findByStartDateTimeLessThanAndStatus(now, TaskStatus.OPEN);
    }

    public Optional<Task> findById(@NonNull String id) {
        return taskRepository.findById(id);
    }

    public Task create(@NonNull Task task) {
        if (task.getId() == null) {
            throw new IllegalArgumentException("Task has no id!");
        }

        // set default values
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.OPEN);
        }

        if (task.getStartDateTime() == null) {
            task.setStartDateTime(clock.instant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }

        // clear the history that is provided by the front-end. The back-end will build it's own history.
        if (task.getHistory() != null) {
            task.getHistory().clear();
        }

        // create the first patch of the task: the patch that defines its creation
        TaskPatch createPatch = taskPatchMapper.mapToPatchThatCreatesATask(task);
        task.patch(createPatch);

        taskPatchRepository.save(createPatch);
        taskRepository.save(task);

        if (task.getFlowId().startsWith(APPLICATION_NAME)) {
            eventPublisher.publishTaskCreated(createPatch);
        }

        return task;
    }

    public List<Task> create(@NonNull Collection<Task> task) {
        return task.stream()
                .map(this::create)
                .collect(Collectors.toList());
    }

    public List<Task> createTasksBasedOn(@NonNull TaskTemplateEntry taskTemplateEntry) {
        var tasks = taskMapper.mapToNewTask(taskTemplateEntry);
        return tasks.stream()
                .map(this::create)
                .collect(Collectors.toList());
    }

    public Task update(@NonNull Task task) {
        taskPatchRepository.saveAll(task.getHistory());
        return taskRepository.save(task);
    }
}
