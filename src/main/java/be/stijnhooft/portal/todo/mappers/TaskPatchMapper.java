package be.stijnhooft.portal.todo.mappers;

import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import be.stijnhooft.portal.todo.utils.ObjectUtils;
import org.springframework.stereotype.Component;

@Component
public class TaskPatchMapper {

    public TaskPatch from(Task task) {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setTaskId(task.getId());
        taskPatch.setDate(task.getCreationDateTime());
        taskPatch.setChanges(ObjectUtils.getAllFieldsAndTheirValues(task));
        return taskPatch;
    }

}
