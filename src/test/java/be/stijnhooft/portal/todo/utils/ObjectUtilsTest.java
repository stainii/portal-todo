package be.stijnhooft.portal.todo.utils;

import be.stijnhooft.portal.todo.model.Importance;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ObjectUtilsTest {

    @Test
    void getAllFieldsAndTheirValues() {
        // arrange
        String name = "original";
        TaskStatus status = TaskStatus.OPEN;
        String description = "originalDescription";
        String context = "originalContext";
        int expectedDurationInHours = 10;
        LocalDateTime dueDateTime = LocalDateTime.of(2020, 1, 1, 1, 1);
        Importance importance = Importance.I_DO_NOT_REALLY_CARE;

        Task task = new Task();
        task.setName(name);
        task.setStatus(status);
        task.setDescription(description);
        task.setContext(context);
        task.setExpectedDurationInHours(expectedDurationInHours);
        task.setStartDateTime(null);
        task.setDueDateTime(dueDateTime);
        task.setImportance(importance);

        // act
        Map<String, String> allFieldsAndTheirValues = ObjectUtils.getAllFieldsAndTheirValues(task);

        // assert
        assertThat(allFieldsAndTheirValues.get("name"), is(equalTo(name)));
        assertThat(allFieldsAndTheirValues.get("status"), is(equalTo(status.toString())));
        assertThat(allFieldsAndTheirValues.get("description"), is(equalTo(description)));
        assertThat(allFieldsAndTheirValues.get("context"), is(equalTo(context)));
        assertThat(allFieldsAndTheirValues.get("expectedDurationInHours"), is(equalTo(String.valueOf(expectedDurationInHours))));
        assertThat(allFieldsAndTheirValues.get("startDateTime"), is(nullValue()));
        assertThat(allFieldsAndTheirValues.get("dueDateTime"), is(equalTo(dueDateTime.toString())));
        assertThat(allFieldsAndTheirValues.get("importance"), is(equalTo(importance.toString())));
        assertThat(allFieldsAndTheirValues.size(), is(equalTo(8)));
    }

}