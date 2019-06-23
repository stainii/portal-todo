package be.stijnhooft.portal.todo.model;

import be.stijnhooft.portal.todo.utils.ObjectUtils;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

    private String taskId;

    @NotNull
    private LocalDateTime date;

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

    public void removeChange(String field) {
        changes.remove(field);
    }
}
