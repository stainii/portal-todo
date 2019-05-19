package be.stijnhooft.portal.todo.controllers;

import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.model.TaskTemplate;
import be.stijnhooft.portal.todo.services.TaskTemplateService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@RestController
@RequestMapping("/template/")
public class TaskTemplateController {

    private final TaskTemplateService taskTemplateService;

    public TaskTemplateController(TaskTemplateService taskTemplateService) {
        this.taskTemplateService = taskTemplateService;
    }

    @RequestMapping(method = GET, value = "/")
    public List<TaskTemplate> findAllTemplates() {
        return taskTemplateService.findAllTemplates();
    }

    @RequestMapping(method = POST, value = "/")
    public TaskTemplate create(@RequestBody TaskTemplate taskTemplate) {
        return taskTemplateService.create(taskTemplate);
    }

    @RequestMapping(method = PUT, value = "/:id")
    public TaskTemplate update(@RequestBody TaskTemplate taskTemplate, @RequestParam("id") Long id) {
        if (!id.equals(taskTemplate.getId())) {
            throw new IllegalArgumentException("The id in the url is not the same as the id in the payload.");
        }
        return taskTemplateService.update(taskTemplate);
    }

    @RequestMapping(method = DELETE, value = "/:id")
    public void delete(@RequestParam("id") Long id) {
        taskTemplateService.delete(id);
    }
}
