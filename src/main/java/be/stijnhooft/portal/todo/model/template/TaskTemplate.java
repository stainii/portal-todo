package be.stijnhooft.portal.todo.model.template;

import be.stijnhooft.portal.todo.model.task.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * A template that can be used to create tasks ({@link Task}).
 * This is useful if the creation of a multiple tasks, related to one goal.
 *
 * A template consists of one or more task definitions. For each task definition, one task will be created.
 * The task definitions describes what its task should look like.
 *
 * When a user wants to create tasks based on a task template, he or she needs to provide
 * a {@link be.stijnhooft.portal.todo.dtos.TaskTemplateEntry}.
 * This entry contains the values that need to be filled in the variables,
 * and the start and due dates. This information will be filled in the task definitions which leads
 * to actual tasks to be created.
 *
 */
@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskTemplate {

    @Id
    private String id;

    private String name;

    private List<TaskDefinition> taskDefinitions;

    /**
     * Names of variables that need to be replaced in certain attributes of the class.
     * @see TaskDefinition#getName()
     * @see TaskDefinition#getContext()
     * @see TaskDefinition#getDescription()
     */
    private List<String> variableNames = new ArrayList<>();

}
