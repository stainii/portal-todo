package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.dtos.TaskTemplateEntry;
import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.model.TaskStatus;
import be.stijnhooft.portal.todo.repositories.TaskRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Slf4j
@Transactional
public class TaskService {

    private final TaskRepository repository;
    private final TaskTemplateService taskTemplateService;

    @Autowired
    public TaskService(TaskRepository repository, TaskTemplateService taskTemplateService) {
        this.repository = repository;
        this.taskTemplateService = taskTemplateService;
    }

    public List<Task> findAllWithStatus(@NonNull TaskStatus status) {
        return this.findAllWithStatus(Collections.singletonList(status));
    }

    public List<Task> findAllWithStatus(@NonNull List<TaskStatus> statuses) {
        if (isEmpty(statuses)) {
            log.warn("Using TaskService.findAllWithStatus with an empty array of statuses to look for.");
        }
        return repository.findAllByStatusIn(statuses);
    }

    public Task create(Task task) {
        return repository.saveAndFlush(task);
    }

    public Task create(TaskTemplateEntry taskTemplateEntry) {
        Task task = taskTemplateService.toTask(taskTemplateEntry);
        return repository.saveAndFlush(task);
    }

    public Task update(Task task) {
        repository.findById(task.getId())
                .orElseThrow(() -> new IllegalArgumentException("Task with id " + task.getId() + " cannot be updated because it does not exist in the database."));

        return repository.saveAndFlush(task);
    }

    public void delete(Long id) {
        repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task with id " + id + " cannot be deleted because it does not exist in the database."));

        repository.deleteById(id);
    }
}
