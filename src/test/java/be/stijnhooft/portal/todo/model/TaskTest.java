package be.stijnhooft.portal.todo.model;

import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.*;

public class TaskTest {

    @Test
    public void patchWhenThereIsNothingToPatch() {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setDate(LocalDateTime.of(2019, 1, 1, 1, 1));

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDuration(Duration.ofSeconds(10));
        task.setStartDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setDueDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setImportance(Importance.I_DO_NOT_REALLY_CARE);

        task.patch(taskPatch);

        assertThat(task.getName(), is("original"));
        assertThat(task.getStatus(), is(TaskStatus.OPEN));
        assertThat(task.getDescription(), is("original"));
        assertThat(task.getContext(), is("original"));
        assertThat(task.getExpectedDuration(), is(Duration.ofSeconds(10)));
        assertThat(task.getStartDateTime(), is(LocalDateTime.of(2019, 1, 1, 1, 1)));
        assertThat(task.getDueDateTime(), is(LocalDateTime.of(2019, 1, 1, 1, 1)));
        assertThat(task.getImportance(), is(Importance.I_DO_NOT_REALLY_CARE));
    }

    @Test
    public void patchWhenAllFieldsArePatched() {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setDate(LocalDateTime.of(2019, 1, 1, 1, 1));
        taskPatch.addChange("name", "new");
        taskPatch.addChange("status", "COMPLETED");
        taskPatch.addChange("description", "new");
        taskPatch.addChange("context", "new");
        taskPatch.addChange("expectedDuration", "PT1h");
        taskPatch.addChange("startDateTime", "2019-02-02T02:02:02");
        taskPatch.addChange("dueDateTime", "2019-02-02T02:02:02");
        taskPatch.addChange("importance", "VERY_IMPORTANT");

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDuration(Duration.ofSeconds(10));
        task.setStartDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setDueDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setImportance(Importance.I_DO_NOT_REALLY_CARE);

        task.patch(taskPatch);

        assertThat(task.getName(), is("new"));
        assertThat(task.getStatus(), is(TaskStatus.COMPLETED));
        assertThat(task.getDescription(), is("new"));
        assertThat(task.getContext(), is("new"));
        assertThat(task.getExpectedDuration(), is(Duration.ofHours(1)));
        assertThat(task.getStartDateTime(), is(LocalDateTime.of(2019, 2, 2,  2, 2, 2)));
        assertThat(task.getDueDateTime(), is(LocalDateTime.of(2019, 2, 2, 2, 2, 2)));
        assertThat(task.getImportance(), is(Importance.VERY_IMPORTANT));
    }

    @Test
    public void patchWhenSomeFieldsArePatched() {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setDate(LocalDateTime.of(2019, 1, 1, 1, 1));
        taskPatch.addChange("name", "new");
        taskPatch.addChange("status", "COMPLETED");
        taskPatch.addChange("description", "new");
        taskPatch.addChange("importance", "VERY_IMPORTANT");

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDuration(Duration.ofSeconds(10));
        task.setStartDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setDueDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setImportance(Importance.I_DO_NOT_REALLY_CARE);

        task.patch(taskPatch);

        assertThat(task.getName(), is("new"));
        assertThat(task.getStatus(), is(TaskStatus.COMPLETED));
        assertThat(task.getDescription(), is("new"));
        assertThat(task.getContext(), is("original"));
        assertThat(task.getExpectedDuration(), is(Duration.ofSeconds(10)));
        assertThat(task.getStartDateTime(), is(LocalDateTime.of(2019, 1, 1, 1, 1)));
        assertThat(task.getDueDateTime(), is(LocalDateTime.of(2019, 1, 1, 1, 1)));
        assertThat(task.getImportance(), is(Importance.VERY_IMPORTANT));
    }

    @Test
    public void patchWhenAllFieldsHaveBeenPatchedAndThereBothAnOlderAndANewerPatch() {
        // set up data set
        TaskPatch taskPatch1 = new TaskPatch();
        taskPatch1.setDate(LocalDateTime.of(2017, 1, 1, 1, 1));
        taskPatch1.addChange("name", "new in 2017");
        taskPatch1.addChange("context", "2017");
        taskPatch1.addChange("description", "2017");

        TaskPatch taskPatch2 = new TaskPatch();
        taskPatch2.setDate(LocalDateTime.of(2018, 1, 1, 1, 1));
        taskPatch2.addChange("name", "new in 2018");
        taskPatch2.addChange("importance", "VERY_IMPORTANT");

        TaskPatch taskPatch3 = new TaskPatch();
        taskPatch3.setDate(LocalDateTime.of(2019, 1, 1, 1, 1));
        taskPatch3.addChange("name", "new in 2019");
        taskPatch3.addChange("description", "2019");

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDuration(Duration.ofSeconds(10));
        task.setStartDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setDueDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setImportance(Importance.I_DO_NOT_REALLY_CARE);

        // add patches
        task.patch(taskPatch1);
        task.patch(taskPatch3);
        // data set is now set up

        // patch with a patch that occurs after patch 1, but before than patch 3.
        task.patch(taskPatch2);

        // the changes in patch 3 should override the changes of the latest applied patch
        assertThat(task.getName(), is("new in 2019")); // patch 3
        assertThat(task.getStatus(), is(TaskStatus.OPEN)); // not patched
        assertThat(task.getDescription(), is("2019")); // patch 3
        assertThat(task.getContext(), is("2017")); // patch 1
        assertThat(task.getExpectedDuration(), is(Duration.ofSeconds(10))); // not patched
        assertThat(task.getStartDateTime(), is(LocalDateTime.of(2019, 1, 1, 1, 1))); // not patched
        assertThat(task.getDueDateTime(), is(LocalDateTime.of(2019, 1, 1, 1, 1))); // not patched
        assertThat(task.getImportance(), is(Importance.VERY_IMPORTANT)); // patch 2
    }

    @Test
    public void patchWhenAllFieldsHaveBeenPatchedAndThereAreNewerPatch() {
        // set up data set
        TaskPatch taskPatch1 = new TaskPatch();
        taskPatch1.setDate(LocalDateTime.of(2017, 1, 1, 1, 1));
        taskPatch1.addChange("name", "new in 2017");
        taskPatch1.addChange("context", "2017");
        taskPatch1.addChange("description", "2017");

        TaskPatch taskPatch2 = new TaskPatch();
        taskPatch2.setDate(LocalDateTime.of(2018, 1, 1, 1, 1));
        taskPatch2.addChange("name", "new in 2018");
        taskPatch2.addChange("importance", "VERY_IMPORTANT");

        TaskPatch taskPatch3 = new TaskPatch();
        taskPatch3.setDate(LocalDateTime.of(2019, 1, 1, 1, 1));
        taskPatch3.addChange("name", "new in 2019");
        taskPatch3.addChange("description", "2019");

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDuration(Duration.ofSeconds(10));
        task.setStartDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setDueDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setImportance(Importance.I_DO_NOT_REALLY_CARE);

        // add patches
        task.patch(taskPatch2);
        task.patch(taskPatch3);
        // data set is now set up

        // patch with a patch that occurs after patch 1, but before than patch 3.
        task.patch(taskPatch1);

        // the changes in patch 3 should override the changes of the latest applied patch
        assertThat(task.getName(), is("new in 2019")); // patch 3
        assertThat(task.getStatus(), is(TaskStatus.OPEN)); // not patched
        assertThat(task.getDescription(), is("2019")); // patch 3
        assertThat(task.getContext(), is("2017")); // patch 1
        assertThat(task.getExpectedDuration(), is(Duration.ofSeconds(10))); // not patched
        assertThat(task.getStartDateTime(), is(LocalDateTime.of(2019, 1, 1, 1, 1))); // not patched
        assertThat(task.getDueDateTime(), is(LocalDateTime.of(2019, 1, 1, 1, 1))); // not patched
        assertThat(task.getImportance(), is(Importance.VERY_IMPORTANT)); // patch 2
    }

    @Test
    public void patchWhenTheTaskHasBeenCompleted() {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setDate(LocalDateTime.of(2019, 1, 1, 1, 1));
        taskPatch.addChange("status", "COMPLETED");

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDuration(Duration.ofSeconds(10));
        task.setStartDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setDueDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setImportance(Importance.I_DO_NOT_REALLY_CARE);

        TaskPatchResult patch = task.patch(taskPatch);

        assertThat(patch.hasBeenCompleted(), is(true));
        assertThat(patch.hasBeenUncompleted(), is(false));
        assertThat(patch.hasBeenRescheduled(), is(false));
        assertThat(patch.getTask(), is(sameInstance(task)));
    }

    @Test
    public void patchWhenTheTaskHasBeenUncompleted() {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setDate(LocalDateTime.of(2019, 1, 1, 1, 1));
        taskPatch.addChange("status", "OPEN");

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.COMPLETED);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDuration(Duration.ofSeconds(10));
        task.setStartDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setDueDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setImportance(Importance.I_DO_NOT_REALLY_CARE);

        TaskPatchResult patch = task.patch(taskPatch);

        assertThat(patch.hasBeenCompleted(), is(false));
        assertThat(patch.hasBeenUncompleted(), is(true));
        assertThat(patch.hasBeenRescheduled(), is(false));
        assertThat(patch.getTask(), is(sameInstance(task)));
    }

    @Test
    public void patchWhenADueDateTimeHasBeenRemoved() {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setDate(LocalDateTime.of(2019, 1, 1, 1, 1));
        taskPatch.addChange("dueDateTime", null);

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDuration(Duration.ofSeconds(10));
        task.setStartDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setDueDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setImportance(Importance.I_DO_NOT_REALLY_CARE);

        TaskPatchResult patch = task.patch(taskPatch);

        assertThat(patch.hasBeenCompleted(), is(false));
        assertThat(patch.hasBeenUncompleted(), is(false));
        assertThat(patch.hasBeenRescheduled(), is(true));
        assertThat(patch.getTask(), is(sameInstance(task)));
    }

    @Test
    public void patchWhenADueDateTimeHasBeenAdded() {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setDate(LocalDateTime.of(2019, 1, 1, 1, 1));
        taskPatch.addChange("dueDateTime", "2019-02-02T02:02:02");

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDuration(Duration.ofSeconds(10));
        task.setStartDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setDueDateTime(null);
        task.setImportance(Importance.I_DO_NOT_REALLY_CARE);

        TaskPatchResult patch = task.patch(taskPatch);

        assertThat(patch.hasBeenCompleted(), is(false));
        assertThat(patch.hasBeenUncompleted(), is(false));
        assertThat(patch.hasBeenRescheduled(), is(true));
        assertThat(patch.getTask(), is(sameInstance(task)));
    }

    @Test
    public void patchWhenTheDueDateTimeHasBeenChanged() {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setDate(LocalDateTime.of(2019, 1, 1, 1, 1));
        taskPatch.addChange("dueDateTime", "2019-02-02T02:02:02");

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDuration(Duration.ofSeconds(10));
        task.setStartDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setDueDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setImportance(Importance.I_DO_NOT_REALLY_CARE);

        TaskPatchResult patch = task.patch(taskPatch);

        assertThat(patch.hasBeenCompleted(), is(false));
        assertThat(patch.hasBeenUncompleted(), is(false));
        assertThat(patch.hasBeenRescheduled(), is(true));
        assertThat(patch.getTask(), is(sameInstance(task)));
    }

}