package be.stijnhooft.portal.todo.mappers;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static be.stijnhooft.portal.todo.PortalTodoApplication.APPLICATION_NAME;

@Component
public class CancellationEventMapper  {

    public Event map(@NonNull TaskPatch taskPatch) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "cancellation");

        return new Event(APPLICATION_NAME, APPLICATION_NAME + "-" + taskPatch.getTaskId(), LocalDateTime.now(), data);
    }
}
