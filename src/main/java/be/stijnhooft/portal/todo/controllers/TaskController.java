package be.stijnhooft.portal.todo.controllers;

import be.stijnhooft.portal.todo.dtos.Source;
import be.stijnhooft.portal.todo.dtos.TaskTemplateEntry;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import be.stijnhooft.portal.todo.model.task.TaskPatchResult;
import be.stijnhooft.portal.todo.services.TaskPatchService;
import be.stijnhooft.portal.todo.services.TaskService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/task/")
public class TaskController {

    private final TaskService taskService;
    private final TaskPatchService taskPatchService;

    public TaskController(TaskService taskService, TaskPatchService taskPatchService) {
        this.taskService = taskService;
        this.taskPatchService = taskPatchService;
    }

    @GetMapping
    public List<Task> getAllActiveTasks() {
        return taskService.findAllActiveTasks();
    }


    @PostMapping
    public Task create(@RequestBody @Valid Task task) {
        return taskService.create(task);
    }

    @PostMapping("/from-template/")
    public List<Task> createFromTemplate(@RequestBody TaskTemplateEntry taskTemplateEntry) {
        return taskService.createTasksBasedOn(taskTemplateEntry);
    }

    /**
     * Why patch, and not update/put?
     *
     * We expect the client to work offline often. When it comes online, the client will send out its updates.
     * Now, when multiple clients update the same task, the last sent change will be applied.
     * To limit possible data loss, it is important to only update the necessary fields.
     *
     * For example:
     * client A updates the status
     * client B updates the description
     *
     * Expected result: both the status and the description is updated.
     * This is only possible when the client only sends out the changed field, instead of the whole object.
     *
     * So, clients are expected to only patch the changed fields.
     */
    @PatchMapping("/{id}")
    public TaskPatchResult patch(@RequestBody @Valid TaskPatch taskPatch, @PathVariable String id) {
        var task = taskService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Task %s not found.", id)));

        taskPatch.setTaskId(id);
        taskPatch.setFlowId(task.getFlowId());

        return taskPatchService.patch(taskPatch, Source.USER);
    }

}
