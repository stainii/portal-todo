package be.stijnhooft.portal.todo.mappers;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import be.stijnhooft.portal.todo.model.task.TaskStatus;
import be.stijnhooft.portal.todo.utils.ObjectUtils;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Map;
import java.util.UUID;

@Component
public class TaskPatchMapper {

    private final Clock clock;

    public TaskPatchMapper(Clock clock) {
        this.clock = clock;
    }

    public TaskPatch from(Task task) {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setId(UUID.randomUUID().toString());
        taskPatch.setTaskId(task.getId());
        taskPatch.setDateTime(task.getCreationDateTime());

        Map<String, String> changes = ObjectUtils.getAllFieldsAndTheirValues(task);
        changes.remove("history");
        taskPatch.setChanges(changes);
        return taskPatch;
    }

    public TaskPatch from(Event event) {
        var taskPatch = new TaskPatch();
        taskPatch.setId(UUID.randomUUID().toString());
        taskPatch.setDateTime(clock.instant());
        taskPatch.setTaskId(event.getFlowId());
        taskPatch.addChange("status", TaskStatus.COMPLETED);
        return taskPatch;
    }
}
