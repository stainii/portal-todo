package be.stijnhooft.portal.todo.model.template;

import be.stijnhooft.portal.todo.model.task.Importance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Duration;

/**
 * The definition of one task in a task template.
 * @see TaskTemplate for more information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDefinition {

    /**
     * Can contain variable names. The variable names need to be defined in attribute {@link TaskTemplate#getVariableNames()}.
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

    private Integer expectedDurationInHours;

    /**
     * Can contain variable names. The variable names need to be defined in attribute {@link TaskTemplate#getVariableNames()}.
     *
     * Example: "Hello, ${user}!"
     */
    @NonNull
    private String context;

    @NonNull
    private Importance importance;

    /**
     * Can contain variable names. The variable names need to be defined in attribute {@link TaskTemplate#getVariableNames()}.
     *
     * Example: "Hello, ${user}!"
     */
    private String description;

}
