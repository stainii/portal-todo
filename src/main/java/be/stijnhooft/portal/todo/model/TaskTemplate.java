package be.stijnhooft.portal.todo.model;

import lombok.*;

import javax.persistence.*;
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
@Entity
@SequenceGenerator( name = "taskIdGenerator",
        sequenceName = "task_id_sequence")
@Table(name = "task_template")
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class TaskTemplate {

    @Id
    @GeneratedValue(generator = "taskIdGenerator")
    private Long id;

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
    @Column(name = "deviation_of_the_main_task_start_date_time")
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
    @Column(name = "deviation_of_the_main_task_due_date_time")
    private Duration deviationOfTheMainTaskDueDateTime;

    @Column(name = "expected_duration")
    private Duration expectedDuration;

    @NonNull
    private String context;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Importance importance;

    private String description;

    @OneToMany
    @JoinColumn(name = "main_task_template_id")
    private List<TaskTemplate> subTaskTemplates = new ArrayList<>();

    /**
     * Names of variables that need to be replaced in certain attributes of the class.
     * @see #name
     * @see #context
     * @see #description
     */
    @ElementCollection
    @CollectionTable(
            name="task_template_variable_name",
            joinColumns=@JoinColumn(name="task_template_id")
    )
    @Column(name="variable_name")
    private List<String> variableNames = new ArrayList<>();

}
