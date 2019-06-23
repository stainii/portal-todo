package be.stijnhooft.portal.todo.events;

import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.model.TaskPatch;
import lombok.*;

@AllArgsConstructor
@Data
@Setter(AccessLevel.PRIVATE)
public class TaskRescheduled implements TaskEvent {

    private TaskPatch taskPatch;

}
