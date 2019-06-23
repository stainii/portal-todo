package be.stijnhooft.portal.todo.mappers;

import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.model.TaskPatch;
import be.stijnhooft.portal.todo.utils.ObjectUtils;
import org.springframework.stereotype.Component;

@Component
public class TaskPatchMapper {

    public TaskPatch from(Task task) {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setTaskId(task.getId());
        taskPatch.setDate(task.getCreationDateTime());

        taskPatch.setChanges(ObjectUtils.getAllFieldsAndTheirValues(task));
        taskPatch.removeChange("subTasks");
        return taskPatch;
    }

}
