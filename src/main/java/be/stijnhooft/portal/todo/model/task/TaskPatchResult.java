package be.stijnhooft.portal.todo.model.task;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Builder
public class TaskPatchResult {

    @Getter
    private Task task;

    @Getter
    private TaskPatch taskPatch;

    private boolean hasBeenCompleted;
    private boolean hasBeenUncompleted;
    private boolean hasBeenRescheduled;

    public boolean hasBeenCompleted() {
        return hasBeenCompleted;
    }

    public boolean hasBeenUncompleted() {
        return hasBeenUncompleted;
    }

    public boolean hasBeenRescheduled() {
        return hasBeenRescheduled;
    }

}
