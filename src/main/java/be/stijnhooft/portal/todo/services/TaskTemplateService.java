package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.dtos.TaskTemplateEntry;
import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.model.TaskStatus;
import be.stijnhooft.portal.todo.model.TaskTemplate;
import be.stijnhooft.portal.todo.repositories.TaskTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static be.stijnhooft.portal.todo.utils.DateTimeUtils.addDurationTo;
import static be.stijnhooft.portal.todo.utils.StringUtils.fillInVariables;

@Service
@Transactional
public class TaskTemplateService {

    private final TaskTemplateRepository repository;
    private final Clock clock;

    @Autowired
    public TaskTemplateService(TaskTemplateRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public Task toTask(TaskTemplateEntry taskTemplateEntry) {
        return toTask(taskTemplateEntry.getTaskTemplate(),
                taskTemplateEntry.getStartDateTimeOfMainTask(),
                taskTemplateEntry.getDueDateTimeOfMainTask(),
                taskTemplateEntry.getVariables());
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

    private Task toTask(TaskTemplate taskTemplate, LocalDateTime startDateTimeOfMainTask,
                        LocalDateTime dueDateTimeOfMainTask, Map<String, String> variables) {
        // fill in all variables in all strings
        String name = fillInVariables(taskTemplate.getName(), variables)
                .orElseThrow(() -> new IllegalArgumentException("The name of a task template cannot be null."));
        String description = fillInVariables(taskTemplate.getDescription(), variables)
                .orElse(null);
        String context = fillInVariables(taskTemplate.getContext(), variables)
                .orElse(null);

        // calculate dates
        LocalDateTime startDateTime = addDurationTo(startDateTimeOfMainTask, taskTemplate.getDeviationOfTheMainTaskStartDateTime())
                .orElse(null);
        LocalDateTime dueDateTime = addDurationTo(dueDateTimeOfMainTask, taskTemplate.getDeviationOfTheMainTaskDueDateTime())
                .orElse(null);

        // other variables
        var expectedDurationInHours = taskTemplate.getExpectedDurationInHours();
        var urgency = taskTemplate.getImportance();

        // do the same for sub tasks
        var subTasks = taskTemplate.getSubTaskTemplates()
                .stream()
                .map(subTask -> toTask(subTask, startDateTimeOfMainTask, dueDateTimeOfMainTask, variables))
                .collect(Collectors.toList());

        // assemble task
        return new Task(null, name, LocalDateTime.ofInstant(clock.instant(), ZoneId.systemDefault()),
                startDateTime, dueDateTime, expectedDurationInHours, context, urgency,
                description, subTasks, TaskStatus.OPEN, null);
    }
}
