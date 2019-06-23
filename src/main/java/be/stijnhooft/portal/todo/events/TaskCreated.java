package be.stijnhooft.portal.todo.events;

import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.model.TaskPatch;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@AllArgsConstructor
@Data
@Setter(AccessLevel.PRIVATE)
public class TaskCreated implements TaskEvent {

    private TaskPatch taskPatch;

}
