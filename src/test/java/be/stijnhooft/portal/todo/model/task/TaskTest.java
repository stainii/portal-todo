package be.stijnhooft.portal.todo.model.task;

import be.stijnhooft.portal.todo.model.Importance;
import be.stijnhooft.portal.todo.utils.ObjectUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import static be.stijnhooft.portal.todo.PortalTodoApplication.APPLICATION_NAME;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class TaskTest {

    @Test
    public void patchWhenThereIsNothingToPatch() {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setFlowId(APPLICATION_NAME + "-12");
        taskPatch.setDateTime(ZonedDateTime.of(2019, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());

        Task task = new Task();
        task.setFlowId(APPLICATION_NAME + "-12");
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDurationInHours(10);
        task.setStartDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setDueDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setImportance(Importance.I_DO_NOT_REALLY_CARE);

        task.patch(taskPatch);

        assertThat(task.getName(), is("original"));
        assertThat(task.getStatus(), is(TaskStatus.OPEN));
        assertThat(task.getDescription(), is("original"));
        assertThat(task.getContext(), is("original"));
        assertThat(task.getExpectedDurationInHours(), is(10));
        assertThat(task.getStartDateTime(), is(LocalDateTime.of(2019, 1, 1, 1, 1)));
        assertThat(task.getDueDateTime(), is(LocalDateTime.of(2019, 1, 1, 1, 1)));
        assertThat(task.getImportance(), is(Importance.I_DO_NOT_REALLY_CARE));
    }

    @Test
    public void patchWhenAllFieldsArePatched() {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setDateTime(ZonedDateTime.of(2019, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        taskPatch.addChange("name", "new");
        taskPatch.addChange("status", "COMPLETED");
        taskPatch.addChange("description", "new");
        taskPatch.addChange("context", "new");
        taskPatch.addChange("expectedDurationInHours", "1");
        taskPatch.addChange("startDateTime", "2019-02-02T02:02:02");
        taskPatch.addChange("dueDateTime", "2019-02-02T02:02:02");
        taskPatch.addChange("importance", "VERY_IMPORTANT");

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDurationInHours(10);
        task.setStartDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setDueDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setImportance(Importance.I_DO_NOT_REALLY_CARE);

        task.patch(taskPatch);

        assertThat(task.getName(), is("new"));
        assertThat(task.getStatus(), is(TaskStatus.COMPLETED));
        assertThat(task.getDescription(), is("new"));
        assertThat(task.getContext(), is("new"));
        assertThat(task.getExpectedDurationInHours(), is(1));
        assertThat(task.getStartDateTime(), is(LocalDateTime.of(2019, 2, 2,  2, 2, 2)));
        assertThat(task.getDueDateTime(), is(LocalDateTime.of(2019, 2, 2, 2, 2, 2)));
        assertThat(task.getImportance(), is(Importance.VERY_IMPORTANT));
    }

    @Test
    public void patchWhenSomeFieldsArePatched() {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setDateTime(ZonedDateTime.of(2019, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        taskPatch.addChange("name", "new");
        taskPatch.addChange("status", "COMPLETED");
        taskPatch.addChange("description", "new");
        taskPatch.addChange("importance", "VERY_IMPORTANT");

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDurationInHours(10);
        task.setStartDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setDueDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setImportance(Importance.I_DO_NOT_REALLY_CARE);

        task.patch(taskPatch);

        assertThat(task.getName(), is("new"));
        assertThat(task.getStatus(), is(TaskStatus.COMPLETED));
        assertThat(task.getDescription(), is("new"));
        assertThat(task.getContext(), is("original"));
        assertThat(task.getExpectedDurationInHours(), is(10));
        assertThat(task.getStartDateTime(), is(LocalDateTime.of(2019, 1, 1, 1, 1)));
        assertThat(task.getDueDateTime(), is(LocalDateTime.of(2019, 1, 1, 1, 1)));
        assertThat(task.getImportance(), is(Importance.VERY_IMPORTANT));
    }

    @Test
    public void patchWhenAllFieldsHaveBeenPatchedAndThereBothAnOlderAndANewerPatch() {
        // set up data set
        TaskPatch taskPatch1 = new TaskPatch();
        taskPatch1.setDateTime(ZonedDateTime.of(2017, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        taskPatch1.addChange("name", "new in 2017");
        taskPatch1.addChange("context", "2017");
        taskPatch1.addChange("description", "2017");

        TaskPatch taskPatch2 = new TaskPatch();
        taskPatch2.setDateTime(ZonedDateTime.of(2018, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        taskPatch2.addChange("name", "new in 2018");
        taskPatch2.addChange("importance", "VERY_IMPORTANT");

        TaskPatch taskPatch3 = new TaskPatch();
        taskPatch3.setDateTime(ZonedDateTime.of(2019, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        taskPatch3.addChange("name", "new in 2019");
        taskPatch3.addChange("description", "2019");

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDurationInHours(10);
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
        assertThat(task.getExpectedDurationInHours(), is(10)); // not patched
        assertThat(task.getStartDateTime(), is(LocalDateTime.of(2019, 1, 1, 1, 1))); // not patched
        assertThat(task.getDueDateTime(), is(LocalDateTime.of(2019, 1, 1, 1, 1))); // not patched
        assertThat(task.getImportance(), is(Importance.VERY_IMPORTANT)); // patch 2
    }

    @Test
    public void patchWhenAllFieldsHaveBeenPatchedAndThereAreNewerPatch() {
        // set up data set
        TaskPatch taskPatch1 = new TaskPatch();
        taskPatch1.setDateTime(ZonedDateTime.of(2017, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        taskPatch1.addChange("name", "new in 2017");
        taskPatch1.addChange("context", "2017");
        taskPatch1.addChange("description", "2017");

        TaskPatch taskPatch2 = new TaskPatch();
        taskPatch2.setDateTime(ZonedDateTime.of(2018, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        taskPatch2.addChange("name", "new in 2018");
        taskPatch2.addChange("importance", "VERY_IMPORTANT");

        TaskPatch taskPatch3 = new TaskPatch();
        taskPatch3.setDateTime(ZonedDateTime.of(2019, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        taskPatch3.addChange("name", "new in 2019");
        taskPatch3.addChange("description", "2019");

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDurationInHours(10);
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
        assertThat(task.getExpectedDurationInHours(), is(10)); // not patched
        assertThat(task.getStartDateTime(), is(LocalDateTime.of(2019, 1, 1, 1, 1))); // not patched
        assertThat(task.getDueDateTime(), is(LocalDateTime.of(2019, 1, 1, 1, 1))); // not patched
        assertThat(task.getImportance(), is(Importance.VERY_IMPORTANT)); // patch 2
    }

    @Test
    public void patchWhenTheTaskHasBeenCompleted() {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setDateTime(ZonedDateTime.of(2019, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        taskPatch.addChange("status", "COMPLETED");

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDurationInHours(10);
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
        taskPatch.setDateTime(ZonedDateTime.of(2019, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        taskPatch.addChange("status", "OPEN");

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.COMPLETED);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDurationInHours(10);
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
        taskPatch.setDateTime(ZonedDateTime.of(2019, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        taskPatch.addChange("dueDateTime", null);

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDurationInHours(10);
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
        taskPatch.setDateTime(ZonedDateTime.of(2019, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        taskPatch.addChange("dueDateTime", "2019-02-02T02:02:02");

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDurationInHours(10);
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
        taskPatch.setDateTime(ZonedDateTime.of(2019, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        taskPatch.addChange("dueDateTime", "2019-02-02T02:02:02");

        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setExpectedDurationInHours(10);
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
    public void undoPatch() {
        // arrange
        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setStartDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setDueDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setImportance(Importance.I_DO_NOT_REALLY_CARE);
        task.setFlowId("Housagotchi-100");

        TaskPatch taskPatch1 = new TaskPatch();
        taskPatch1.setDateTime(ZonedDateTime.of(2019, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        Map<String, String> changes = ObjectUtils.getAllFieldsAndTheirValues(task);
        changes.remove("history");
        taskPatch1.setChanges(changes);

        TaskPatch taskPatch2 = new TaskPatch();
        taskPatch2.setDateTime(ZonedDateTime.of(2019, 1, 2, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        taskPatch2.addChange("dueDateTime", "2019-03-03T03:03:03");
        taskPatch2.addChange("description", "new description");
        taskPatch2.addChange("importance", "NOT_SO_IMPORTANT");
        taskPatch2.addChange("expectedDurationInHours", "10");


        TaskPatch taskPatch3 = new TaskPatch();
        taskPatch3.setDateTime(ZonedDateTime.of(2019, 1, 3, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        taskPatch3.addChange("dueDateTime", "2019-04-04T04:04:04");
        taskPatch3.addChange("importance", "IMPORTANT");

        TaskPatch taskPatch4 = new TaskPatch();
        taskPatch4.setDateTime(ZonedDateTime.of(2019, 1, 4, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        taskPatch4.addChange("importance", "NOT_SO_IMPORTANT");

        task.patch(taskPatch1);
        task.patch(taskPatch2);
        task.patch(taskPatch3);
        task.patch(taskPatch4);

        // act
        TaskPatchResult taskPatchResult = task.undoPatch(taskPatch2);

        // assert
        assertThat(taskPatchResult.getTask(), is(equalTo(task)));
        assertThat(taskPatchResult.getTaskPatch().getDateTime(), is(notNullValue()));
        assertThat(taskPatchResult.getTaskPatch().getFlowId(), is(equalTo(task.getFlowId())));
        assertThat(taskPatchResult.getTaskPatch().getTaskId(), is(equalTo(task.getId())));
        assertThat(taskPatchResult.getTaskPatch().getChange("dueDateTime"), is(equalTo("2019-04-04T04:04:04")));
        assertThat(taskPatchResult.getTaskPatch().getChange("description"), is(equalTo("original")));
        assertThat(taskPatchResult.getTaskPatch().getChange("expectedDurationInHours"), is(nullValue()));
        assertThat(taskPatchResult.getTaskPatch().getChanges().size(), is(equalTo(3)));
    }

    @Test
    public void undoPatchWhenUndoingCreation() {
        // arrange
        Task task = new Task();
        task.setName("original");
        task.setStatus(TaskStatus.OPEN);
        task.setDescription("original");
        task.setContext("original");
        task.setStartDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setDueDateTime(LocalDateTime.of(2019, 1, 1, 1, 1));
        task.setImportance(Importance.I_DO_NOT_REALLY_CARE);
        task.setFlowId("Housagotchi-100");

        TaskPatch creationPatch = new TaskPatch();
        creationPatch.setDateTime(ZonedDateTime.of(2019, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()).toInstant());
        Map<String, String> changes = ObjectUtils.getAllFieldsAndTheirValues(task);
        changes.remove("history");
        creationPatch.setChanges(changes);

        task.getHistory().clear();
        task.getHistory().add(creationPatch);

        // act
        TaskPatchResult taskPatchResult = task.undoPatch(creationPatch);

        // assert
        assertThat(task.getStatus(), is(TaskStatus.COMPLETED));

        assertThat(task.getHistory(), hasSize(2));
        assertThat(task.getHistory().get(1).getDateTime(), is(notNullValue()));
        assertThat(task.getHistory().get(1).getFlowId(), is(equalTo(task.getFlowId())));
        assertThat(task.getHistory().get(1).getTaskId(), is(equalTo(task.getId())));
        assertThat(task.getHistory().get(1).getChange("status"), is(equalTo("COMPLETED")));
        assertThat(task.getHistory().get(1).getChanges().size(), is(equalTo(1)));

        assertThat(taskPatchResult.getTask(), is(equalTo(task)));
        assertThat(taskPatchResult.getTaskPatch().getDateTime(), is(notNullValue()));
        assertThat(taskPatchResult.getTaskPatch().getFlowId(), is(equalTo(task.getFlowId())));
        assertThat(taskPatchResult.getTaskPatch().getTaskId(), is(equalTo(task.getId())));
        assertThat(taskPatchResult.getTaskPatch().getChange("status"), is(equalTo("COMPLETED")));
        assertThat(taskPatchResult.getTaskPatch().getChanges().size(), is(equalTo(1)));

    }
}
