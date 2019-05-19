package be.stijnhooft.portal.todo.controllers;

import be.stijnhooft.portal.todo.dtos.TaskTemplateEntry;
import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.model.TaskStatus;
import be.stijnhooft.portal.todo.services.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/task/")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @RequestMapping(method = GET, value = "/")
    public List<Task> findAllWithStatus(@RequestParam(required = false, defaultValue = "open") List<String> statuses) {
        List<TaskStatus> parsedTaskStatuses =
                statuses.stream()
                        .map(TaskStatus::parse)
                        .collect(Collectors.toList());
        return taskService.findAllWithStatus(parsedTaskStatuses);
    }

    @RequestMapping(method = POST, value = "/from-template/")
    public Task createFromTemplate(@RequestBody TaskTemplateEntry taskTemplateEntry) {
        return taskService.create(taskTemplateEntry);
    }

    @RequestMapping(method = POST, value = "/")
    public Task create(@RequestBody Task task) {
        return taskService.create(task);
    }

    @RequestMapping(method = PUT, value = "/:id")
    public Task update(@RequestBody Task task, @RequestParam("id") Long id) {
        if (!id.equals(task.getId())) {
            throw new IllegalArgumentException("The id in the url is not the same as the id in the payload.");
        }
        return taskService.update(task);
    }

    @RequestMapping(method = DELETE, value = "/:id")
    public void delete(@RequestParam("id") Long id) {
        taskService.delete(id);
    }

}
