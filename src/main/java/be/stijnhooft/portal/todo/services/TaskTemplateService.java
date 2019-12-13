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
import java.util.ArrayList;
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
        for (TaskTemplate subTaskTemplate : taskTemplate.getFollowUpTaskTemplates()) {
            create(subTaskTemplate);
        }
        return repository.save(taskTemplate);
    }

    public TaskTemplate update(TaskTemplate taskTemplate) {
        delete(taskTemplate.getId());
        return create(taskTemplate);
    }

    public void delete(String id) {
        repository.findById(id)
                .ifPresent(this::delete);
    }

    public void delete(TaskTemplate taskTemplate) {
        for (TaskTemplate subTaskTemplate : taskTemplate.getFollowUpTaskTemplates()) {
            delete(subTaskTemplate);
        }
        repository.delete(taskTemplate);
    }

    private List<Task> toTasks(TaskTemplate taskTemplate, LocalDateTime startDateTimeOfMainTask,
                               LocalDateTime dueDateTimeOfMainTask, Map<String, String> variables) {
        // fill in all variables in all strings
        String name = fillInVariables(taskTemplate.getName(), variables)
                .orElseThrow(() -> new IllegalArgumentException("The name of a task template cannot be null."));
        String description = fillInVariables(taskTemplate.getDescription(), variables)
                .orElse(null);
        String context = fillInVariables(taskTemplate.getContext(), variables)
                .orElse(null);

        // calculate dates
        var startDateTime = addDurationTo(startDateTimeOfMainTask, taskTemplate.getDeviationOfTheMainTaskStartDateTime())
                .orElse(null);
        var dueDateTime = addDurationTo(dueDateTimeOfMainTask, taskTemplate.getDeviationOfTheMainTaskDueDateTime())
                .orElse(null);

        // other variables
        var expectedDurationInHours = taskTemplate.getExpectedDurationInHours();
        var urgency = taskTemplate.getImportance();

        // assemble task
        var task = new Task(null, name, LocalDateTime.ofInstant(clock.instant(), ZoneId.systemDefault()),
                startDateTime, dueDateTime, expectedDurationInHours, context, urgency,
                description, TaskStatus.OPEN, null);

        // do the same for follow-up tasks
        var followUpTasks = taskTemplate.getFollowUpTaskTemplates()
                .stream()
                .flatMap(subTask -> toTasks(subTask, startDateTimeOfMainTask, dueDateTimeOfMainTask, variables).stream())
                .collect(Collectors.toList());

        var allTasks = new ArrayList<Task>();
        allTasks.add(task);
        allTasks.addAll(followUpTasks);
        return allTasks;
    }
}
