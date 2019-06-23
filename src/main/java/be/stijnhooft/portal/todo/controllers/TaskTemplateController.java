package be.stijnhooft.portal.todo.controllers;

import be.stijnhooft.portal.todo.model.TaskTemplate;
import be.stijnhooft.portal.todo.services.TaskTemplateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/template/")
public class TaskTemplateController {

    private final TaskTemplateService taskTemplateService;

    public TaskTemplateController(TaskTemplateService taskTemplateService) {
        this.taskTemplateService = taskTemplateService;
    }

    @GetMapping
    public List<TaskTemplate> findAllTemplates() {
        return taskTemplateService.findAllTemplates();
    }

    @PostMapping
    public TaskTemplate create(@RequestBody TaskTemplate taskTemplate) {
        return taskTemplateService.create(taskTemplate);
    }

    @PutMapping("/{id}")
    public TaskTemplate update(@RequestBody TaskTemplate taskTemplate, @PathVariable("id") String id) {
        if (!id.equals(taskTemplate.getId())) {
            throw new IllegalArgumentException("The id in the url is not the same as the id in the payload.");
        }
        return taskTemplateService.update(taskTemplate);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        taskTemplateService.delete(id);
    }
}
