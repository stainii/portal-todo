package be.stijnhooft.portal.todo.events;

import be.stijnhooft.portal.todo.model.task.TaskPatch;

public interface TaskEvent {

    TaskPatch getTaskPatch();

}
