package be.stijnhooft.portal.todo.events;

import be.stijnhooft.portal.todo.model.task.TaskPatch;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@AllArgsConstructor
@Data
@Setter(AccessLevel.PRIVATE)
public class TaskRescheduled implements TaskEvent {

    private TaskPatch taskPatch;

}
