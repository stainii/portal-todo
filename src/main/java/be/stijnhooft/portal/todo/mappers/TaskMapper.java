package be.stijnhooft.portal.todo.mappers;

import be.stijnhooft.portal.todo.dtos.FiringSubscription;
import be.stijnhooft.portal.todo.dtos.TaskTemplateEntry;
import be.stijnhooft.portal.todo.model.Importance;
import be.stijnhooft.portal.todo.model.subscription.SubscriptionMappingToTask;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskStatus;
import be.stijnhooft.portal.todo.model.template.TaskTemplate;
import be.stijnhooft.portal.todo.utils.DateTimeUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static be.stijnhooft.portal.todo.utils.DateTimeUtils.addDaysTo;
import static be.stijnhooft.portal.todo.utils.StringUtils.fillInVariables;

@Slf4j
@Component
public class TaskMapper {

    private final ExpressionParser parser;
    private final Clock clock;

    public TaskMapper(Clock clock) {
        this.clock = clock;
        this.parser = new SpelExpressionParser();
    }

    public Task map(@NonNull FiringSubscription firingSubscription) {
        var event = firingSubscription.getEvent();
        var mapping = firingSubscription.getSubscription().getMappingToTask();

        var evaluationContext = new StandardEvaluationContext(event);
        var name = parser.parseExpression(mapping.ofName())
            .getValue(evaluationContext, String.class);
        var description = parseDescription(mapping, evaluationContext);
        var context = parser.parseExpression(mapping.ofContext())
                .getValue(evaluationContext, String.class);
        var dueDateTime = parseDueDateTime(mapping, evaluationContext);
        var importance = parseImportance(mapping, evaluationContext);

        return new Task(event.getFlowId(), name, clock.instant(),
                null, dueDateTime, null, context, importance,
                description, TaskStatus.OPEN, null);
    }

    public List<Task> map(TaskTemplateEntry taskTemplateEntry) {
        return map(taskTemplateEntry.getTaskTemplate(),
                taskTemplateEntry.getStartDateTimeOfMainTask(),
                taskTemplateEntry.getDueDateTimeOfMainTask(),
                taskTemplateEntry.getVariables());
    }

    private Importance parseImportance(SubscriptionMappingToTask mapping, StandardEvaluationContext context) {
        if (mapping.ofImportance() == null) {
            return null;
        }
        return Importance.valueOf(
                parser.parseExpression(mapping.ofImportance())
                .getValue(context, String.class)
        );
    }

    private LocalDateTime parseDueDateTime(SubscriptionMappingToTask mapping, StandardEvaluationContext context) {
        if (mapping.ofDueDate() == null) {
            return null;
        }
        return DateTimeUtils.parseAsLocalDateTime(
                parser.parseExpression(mapping.ofDueDate())
                .getValue(context, String.class)
        );
    }

    private String parseDescription(SubscriptionMappingToTask mapping, StandardEvaluationContext evaluationContext) {
        if (mapping.ofDescription() == null) {
            return null;
        }
        return parser.parseExpression(mapping.ofDescription())
                .getValue(evaluationContext, String.class);
    }



    private List<Task> map(TaskTemplate taskTemplate, LocalDateTime startDateTimeOfMainTask,
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
                    var importance = taskDefinition.getImportance();

                    // assemble task
                    return new Task(UUID.randomUUID().toString(), name, clock.instant(),
                            startDateTime, dueDateTime, expectedDurationInHours, context, importance,
                            description, TaskStatus.OPEN, null);
                })
                .collect(Collectors.toList());
    }

}
