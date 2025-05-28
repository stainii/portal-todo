package be.stijnhooft.portal.todo.model.task;

import be.stijnhooft.portal.todo.model.Importance;
import be.stijnhooft.portal.todo.utils.DateTimeUtils;
import be.stijnhooft.portal.todo.utils.ObjectUtils;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static be.stijnhooft.portal.todo.PortalTodoApplication.APPLICATION_NAME;
import static java.util.Objects.requireNonNullElseGet;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Task {

    @Id
    private String id;

    private String flowId;

    @NonNull
    private String name;

    @NonNull
    private Instant creationDateTime;

    /**
     * Optional date time, which indicates when a task becomes relevant to pick up.
     * Before this date time, the task should not be shown to the user.
     **/
    private LocalDateTime startDateTime;

    private LocalDateTime dueDateTime;

    private Integer expectedDurationInHours;

    @NonNull
    private String context;

    private Importance importance;

    private String description;

    @NonNull
    private TaskStatus status;

    @Setter(AccessLevel.PRIVATE)
    @DBRef
    private List<TaskPatch> history = new ArrayList<>();

    public String getFlowId() {
        return requireNonNullElseGet(flowId, () -> "%s-%s".formatted(APPLICATION_NAME, id));
    }

    @SuppressWarnings("Convert2MethodRef")
    public TaskPatchResult patch(TaskPatch taskPatch) {
        TaskStatus statusBeforePatch = this.getStatus();
        LocalDateTime dueDateTimeBeforePatch = this.getDueDateTime();

        // apply patch
        patchIfNeeded(taskPatch, "name", newValue -> this.setName(newValue));
        patchIfNeeded(taskPatch, "startDateTime", newValue -> this.setStartDateTime(DateTimeUtils.parseAsLocalDateTime(newValue)));
        patchIfNeeded(taskPatch, "dueDateTime", newValue -> this.setDueDateTime(DateTimeUtils.parseAsLocalDateTime(newValue)));
        patchIfNeeded(taskPatch, "expectedDurationInHours", newValue -> this.setExpectedDurationInHours(newValue == null ? null : Integer.parseInt(newValue)));
        patchIfNeeded(taskPatch, "context", newValue -> this.setContext(newValue));
        patchIfNeeded(taskPatch, "importance", newValue -> this.setImportance(newValue == null ? null : Importance.valueOf(newValue)));
        patchIfNeeded(taskPatch, "description", newValue -> this.setDescription(newValue));
        patchIfNeeded(taskPatch, "status", newValue -> this.setStatus(newValue == null ? null : TaskStatus.valueOf(newValue)));

        // add patch to history
        if (history == null) {
            history = new ArrayList<>();
        }
        history.add(taskPatch);

        // reapply newer patches
        history.stream()
                .filter(otherUpdate -> otherUpdate.getDateTime().isAfter(taskPatch.getDateTime()))
                .findFirst()
                .ifPresent(this::patch);

        // create a response summarizing details of what has changed
        return calculateTaskPatchResult(taskPatch, statusBeforePatch, dueDateTimeBeforePatch);
    }

    private void patchIfNeeded(TaskPatch taskPatch, String field, Consumer<String> action) {
        if (taskPatch.containsChange(field)) {
            action.accept(taskPatch.getChange(field));
        }
    }

    public TaskPatchResult undoPatch(TaskPatch taskPatch) {
        HashMap<String, String> revertedChanges = calculateUndoPatchChanges(taskPatch);

        var undoPatch = TaskPatch.builder()
                .dateTime(Instant.now())
                .taskId(id)
                .flowId(getFlowId())
                .changes(revertedChanges)
                .build();

        return patch(undoPatch);
    }

    private HashMap<String, String> calculateUndoPatchChanges(TaskPatch taskPatchToUndo) {
        int patchIndexInHistory = history.indexOf(taskPatchToUndo);
        if (patchIndexInHistory < 0) {
            throw new IllegalArgumentException("Patch with id " + taskPatchToUndo.getId() + " is not in the history of task with id " + this.id);
        } else if (patchIndexInHistory == 0) {
            // reverting the "create task" patch. That can't actually be done: once a task is made it is made. We do the next best thing: completing the task
            var changes = new HashMap<String, String>();
            changes.put("status", "COMPLETED");
            return changes;
        } else {
            Task copyOfTaskWithPatchReverted = new Task();
            history.stream()
                    .filter(taskPatch -> !taskPatch.equals(taskPatchToUndo))
                    .forEach(copyOfTaskWithPatchReverted::patch);

            // check what has been reverted and which are the new values
            var revertedChanges = new HashMap<String, String>();
            for (String change : taskPatchToUndo.getChanges().keySet()) {
                Object taskValue = copyOfTaskWithPatchReverted.get(change);
                String revertedPatchValue = taskPatchToUndo.getChange(change);
                if (ObjectUtils.notEqual(taskValue, revertedPatchValue)) {
                    if (taskValue == null) {
                        revertedChanges.put(change, null);
                    } else {
                        revertedChanges.put(change, taskValue.toString());
                    }
                }
            }
            return revertedChanges;
        }
    }

    private TaskPatchResult calculateTaskPatchResult(TaskPatch taskPatch, TaskStatus statusBeforePatch, LocalDateTime dueDateTimeBeforePatch) {
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

    @SneakyThrows
    private Object get(String field) {
        return ObjectUtils.getAllFieldsAndTheirValues(this)
                .get(field);
    }

}
