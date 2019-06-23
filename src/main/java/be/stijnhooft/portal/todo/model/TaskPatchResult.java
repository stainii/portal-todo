package be.stijnhooft.portal.todo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
