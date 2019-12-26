package be.stijnhooft.portal.todo.model.task;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class that contains updates of specific fields of a task.
 * These changes should have been reflected in the task.
 */
@Document(collection = "taskPatch")
@Data
@NoArgsConstructor
public class TaskPatch {

    @Id
    private String id;

    @NotNull
    private String taskId;

    @NotNull
    private Instant dateTime;

    private Map<String, String> changes = new LinkedHashMap<>();

    @JsonAnySetter
    public void addChange(String key, Object value) {
        changes.put(key, value == null ? null : value.toString());
    }

    public boolean containsChange(String field) {
        return changes.containsKey(field);
    }

    public String getChange(String field) {
        return changes.get(field);
    }

}
