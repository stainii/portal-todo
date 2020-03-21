package be.stijnhooft.portal.todo.mappers;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import be.stijnhooft.portal.todo.model.task.TaskStatus;
import be.stijnhooft.portal.todo.repositories.TaskRepository;
import be.stijnhooft.portal.todo.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class TaskPatchMapper {

    private final Clock clock;
    private final TaskRepository taskRepository;

    public TaskPatchMapper(Clock clock, TaskRepository taskRepository) {
        this.clock = clock;
        this.taskRepository = taskRepository;
    }

    public TaskPatch mapToPatchThatCreatesATask(Task task) {
        Map<String, String> changes = ObjectUtils.getAllFieldsAndTheirValues(task);
        changes.remove("history");

        return TaskPatch.builder()
                .id(UUID.randomUUID().toString())
                .taskId(task.getId())
                .flowId(task.getFlowId())
                .dateTime(task.getCreationDateTime())
                .changes(changes)
                .build();
    }

    public Optional<TaskPatch> mapToTaskPatchThatCompletesATask(Event event) {
        var flowId = event.getFlowId();
        return taskRepository.findFirstByFlowIdOrderByCreationDateTimeDesc(flowId)
                .map(task -> {
                    log.debug("Matching task with id {} to event with flowId {}", task.getId(), flowId);

                    var taskPatch = TaskPatch.builder()
                            .id(UUID.randomUUID().toString())
                            .taskId(task.getId())
                            .flowId(flowId)
                            .dateTime(clock.instant())
                            .build();
                    taskPatch.addChange("status", TaskStatus.COMPLETED);
                    return taskPatch;
                });
    }
}
