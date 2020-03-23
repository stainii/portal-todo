package be.stijnhooft.portal.todo.model.template;

import be.stijnhooft.portal.todo.model.Importance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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
     * Used to calculate the start date of this task, compared to the start date/due date of the main task.
     *
     * Example: when task A can be started, a week later task B can be picked up.
     * The deviation days of task A, the main task, is 0.
     * The deviation days of task B, the sub task, is 7. The deviation base is due date.
     * => The start date of task B will be 7 days later than the due date of task A.
     *
     * If null, no start date will be set for the resulting task.
     */
    private Integer startDateDeviationDays;

    private DeviationBase startDateDeviationBase;

    /**
     * Used to calculate the due date of this task, compared to the due date of the main task.
     * Useful for sub tasks.
     *
     * Example: when task A has to be done, a week later task B should be picked up.
     * The deviation of task A, the main task, is 0.
     * The deviation of task B, the sub task, is 7. The deviation base is due date.
     * => The start date of task B will be 7 days later than the due date of task A.
     *
     * If null, no due date will be set for the resulting task.
     */
    private Integer dueDateDeviationDays;
    private DeviationBase dueDateDeviationBase;

    private Integer expectedDurationInHours;

    /**
     * Can contain variable names. The variable names need to be defined in attribute {@link TaskTemplate#getVariableNames()}.
     *
     * Example: "Hello, ${user}!"
     */
    @NonNull
    private String context;

    private Importance importance;

    /**
     * Can contain variable names. The variable names need to be defined in attribute {@link TaskTemplate#getVariableNames()}.
     *
     * Example: "Hello, ${user}!"
     */
    private String description;

}
