package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.model.template.TaskTemplate;
import be.stijnhooft.portal.todo.repositories.TaskTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskTemplateService {

    private final TaskTemplateRepository repository;

    public TaskTemplateService(TaskTemplateRepository repository) {
        this.repository = repository;
    }

    public List<TaskTemplate> findAllTemplates() {
        return repository.findAll();
    }

    public TaskTemplate create(TaskTemplate taskTemplate) {
        return repository.save(taskTemplate);
    }

    public TaskTemplate update(TaskTemplate taskTemplate) {
        return repository.save(taskTemplate);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

}
