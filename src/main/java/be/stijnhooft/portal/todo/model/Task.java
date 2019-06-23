package be.stijnhooft.portal.todo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Task {

    @Id
    private String id;

    @NonNull
    private String name;

    @NonNull
    private LocalDateTime creationDateTime;

    /**
     * Optional date time, which indicates when a task becomes relevant to pick up.
     * Before this date time, the task should not be shown to the user.
     **/
    private LocalDateTime startDateTime;

    private LocalDateTime dueDateTime;

    private Duration expectedDuration;

    @NonNull
    private String context;

    @NonNull
    private Importance importance;

    private String description;

    private List<Task> subTasks = new ArrayList<>();

    @NonNull
    private TaskStatus status;

    @Setter(AccessLevel.PRIVATE)
    @DBRef
    private List<TaskPatch> history = new ArrayList<>();

    public TaskPatchResult patch(TaskPatch taskPatch) {
        TaskStatus statusBeforePatch = this.getStatus();
        LocalDateTime dueDateTimeBeforePatch = this.getDueDateTime();

        // apply patch
        patchIfNeeded(taskPatch, "name", newValue -> this.setName(newValue));
        patchIfNeeded(taskPatch, "startDateTime", newValue -> this.setStartDateTime(newValue == null ? null : LocalDateTime.parse(newValue)));
        patchIfNeeded(taskPatch, "dueDateTime", newValue -> this.setDueDateTime(newValue == null ? null : LocalDateTime.parse(newValue)));
        patchIfNeeded(taskPatch, "expectedDuration", newValue -> this.setExpectedDuration(newValue == null ? null : Duration.parse(newValue)));
        patchIfNeeded(taskPatch, "context", newValue -> this.setContext(newValue));
        patchIfNeeded(taskPatch, "importance", newValue -> this.setImportance(newValue == null ? null : Importance.valueOf(newValue)));
        patchIfNeeded(taskPatch, "description", newValue -> this.setDescription(newValue));
        patchIfNeeded(taskPatch, "status", newValue -> this.setStatus(newValue == null ? null : TaskStatus.valueOf(newValue)));

        // reapply newer patches
        history.stream()
                .filter(otherUpdate -> otherUpdate.getDate().isAfter(taskPatch.getDate()))
                .findFirst()
                .ifPresent(this::patch);

        // add patch to history
        history.add(taskPatch);

        // create a response summarizing details of what has changed
        return TaskPatchResult.builder()
                .task(this)
                .taskPatch(taskPatch)
                .hasBeenCompleted(statusBeforePatch != TaskStatus.COMPLETED
                                    && this.getStatus() == TaskStatus.COMPLETED)
                .hasBeenUncompleted(statusBeforePatch == TaskStatus.COMPLETED
                                    && this.getStatus() != TaskStatus.COMPLETED)
                .hasBeenRescheduled(!Objects.equals(dueDateTimeBeforePatch, this.getDueDateTime()))
                .build();
    }

    private void patchIfNeeded(TaskPatch taskPatch, String field, Consumer<String> action) {
        if (taskPatch.containsChange(field)) {
            action.accept(taskPatch.getChange(field));
        }
    }
}
