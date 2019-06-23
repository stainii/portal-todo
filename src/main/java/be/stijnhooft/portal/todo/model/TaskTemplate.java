package be.stijnhooft.portal.todo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * A template that can be used to create tasks ({@link Task}).
 * This is useful if the creation of a certain task always leads to the creation of subtasks.
 * By choosing a template, selecting the main task's due date and optionally providing variable values, multiple tasks can be created at once.
 *
 * In order to create a task, a {@link be.stijnhooft.portal.todo.dtos.TaskTemplateEntry} needs to be provided.
 * This entry contains the values that need to be filled in the variables,
 * and the due date of the main task, from which deviations for sub tasks' due dates can be calculated.
 *
 */
@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class TaskTemplate {

    @Id
    private String id;

    /**
     * Can contain variable names. The variable names need to be defined in attribute {@link #variableNames}.
     *
     * Example: "Hello, ${user}!"
     */
    @NonNull
    private String name;

    /**
     * Used to calculate the start date of this task, compared to the start date of the main task.
     * Useful for sub tasks.
     *
     * Example: when task A can be started, a week later task B can be picked up.
     * The deviation of task A, the main task, is 0.
     * The deviation of task B, the sub task, is 7 days.
     *
     * If null, no start date will be set for the resulting task.
     */
    private Duration deviationOfTheMainTaskStartDateTime;

    /**
     * Used to calculate the due date of this task, compared to the due date of the main task.
     * Useful for sub tasks.
     *
     * Example: when task A has to be done, a week later task B should be picked up.
     * The deviation of task A, the main task, is 0.
     * The deviation of task B, the sub task, is 7 days.
     *
     * If null, no due date will be set for the resulting task.
     */
    private Duration deviationOfTheMainTaskDueDateTime;

    private Duration expectedDuration;

    @NonNull
    private String context;

    @NonNull
    private Importance importance;

    private String description;

    private List<TaskTemplate> subTaskTemplates = new ArrayList<>();

    /**
     * Names of variables that need to be replaced in certain attributes of the class.
     * @see #name
     * @see #context
     * @see #description
     */
    private List<String> variableNames = new ArrayList<>();

}
