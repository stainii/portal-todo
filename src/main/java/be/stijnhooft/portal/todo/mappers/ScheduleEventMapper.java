package be.stijnhooft.portal.todo.mappers;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.model.TaskPatch;
import be.stijnhooft.portal.todo.repositories.TaskRepository;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import static be.stijnhooft.portal.todo.PortalTodoApplication.APPLICATION_NAME;

@Component
public class ScheduleEventMapper {

    private final TaskRepository taskRepository;
    private final Clock clock;

    public ScheduleEventMapper(TaskRepository taskRepository, Clock clock) {
        this.taskRepository = taskRepository;
        this.clock = clock;
    }

    public Event map(@NonNull TaskPatch taskPatch) {
        Task task = taskRepository.findById(taskPatch.getTaskId())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find task with id " + taskPatch.getTaskId()));

        Map<String, String> data = new HashMap<>();
        data.put("type", "schedule");
        data.put("task", task.getName());

        if (task.getDueDateTime() != null) {
            data.put("dueDate", task.getDueDateTime().toString());
        }

        return new Event(APPLICATION_NAME, APPLICATION_NAME + "-" + taskPatch.getTaskId(), LocalDateTime.ofInstant(clock.instant(), ZoneId.systemDefault()), data);
    }
}
