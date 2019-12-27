package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.dtos.TaskTemplateEntry;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskStatus;
import be.stijnhooft.portal.todo.model.template.TaskTemplate;
import be.stijnhooft.portal.todo.repositories.TaskTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static be.stijnhooft.portal.todo.utils.DateTimeUtils.addDaysTo;
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

    public List<Task> toTasks(TaskTemplateEntry taskTemplateEntry) {
        return toTasks(taskTemplateEntry.getTaskTemplate(),
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

    private List<Task> toTasks(TaskTemplate taskTemplate, LocalDateTime startDateTimeOfMainTask,
                               LocalDateTime dueDateTimeOfMainTask, Map<String, String> variables) {
        return taskTemplate.getTaskDefinitions()
                .stream()
                .map(taskDefinition -> {
                    // fill in all variables in all strings
                    String name = fillInVariables(taskDefinition.getName(), variables)
                            .orElse("No name");
                    String description = fillInVariables(taskDefinition.getDescription(), variables)
                            .orElse(null);
                    String context = fillInVariables(taskDefinition.getContext(), variables)
                            .orElse(null);

                    // calculate dates
                    var startDateTime = addDaysTo(startDateTimeOfMainTask, taskDefinition.getDeviationOfTheMainTaskStartDateTimeInDays())
                            .orElse(null);
                    var dueDateTime = addDaysTo(dueDateTimeOfMainTask, taskDefinition.getDeviationOfTheMainTaskDueDateTimeInDays())
                            .orElse(null);

                    // other variables
                    var expectedDurationInHours = taskDefinition.getExpectedDurationInHours();
                    var urgency = taskDefinition.getImportance();

                    // assemble task
                    return new Task(UUID.randomUUID().toString(), name, clock.instant(),
                            startDateTime, dueDateTime, expectedDurationInHours, context, urgency,
                            description, TaskStatus.OPEN, null);
                })
                .collect(Collectors.toList());
    }
}
