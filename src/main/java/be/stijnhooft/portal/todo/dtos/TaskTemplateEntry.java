package be.stijnhooft.portal.todo.dtos;

import be.stijnhooft.portal.todo.model.template.TaskTemplate;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO that can be used to transform a task template into actual tasks.
 * Contains the task template,
 * the variables that need to be filled in
 * and the due date of the main tasks that needs to be created.
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class TaskTemplateEntry {

    @NonNull
    private TaskTemplate taskTemplate;

    @NonNull
    private Map<String, String> variables;

    private LocalDateTime startDateTimeOfMainTask;

    private LocalDateTime dueDateTimeOfMainTask;

}
