package be.stijnhooft.portal.todo.mappers;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.model.domain.FlowAction;
import be.stijnhooft.portal.todo.dtos.FiringSubscription;
import be.stijnhooft.portal.todo.dtos.TaskTemplateEntry;
import be.stijnhooft.portal.todo.model.Importance;
import be.stijnhooft.portal.todo.model.subscription.Subscription;
import be.stijnhooft.portal.todo.model.subscription.SubscriptionMappingToTask;
import be.stijnhooft.portal.todo.model.task.TaskStatus;
import be.stijnhooft.portal.todo.model.template.TaskDefinition;
import be.stijnhooft.portal.todo.model.template.TaskTemplate;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TaskMapperTest {

    private TaskMapper taskMapper;
    private Clock clock = Clock.fixed(ZonedDateTime.of(2019, 10, 10, 10, 10, 10, 10, ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

    @Before
    public void init() {
        taskMapper = new TaskMapper(clock);
    }

    @Test
    public void mapTaskTemplateToNewTask() {
        // arrange
        var taskTemplate = new TaskTemplate();
        taskTemplate.setName("Organize a workshop");
        taskTemplate.setVariableNames(Arrays.asList("subject", "school", "speakers"));

        var taskDefinition1 = new TaskDefinition();
        taskDefinition1.setName("Ask speaker for a workshop about ${subject} at ${school}");
        taskDefinition1.setDeviationOfTheMainTaskStartDateTimeInDays(0);
        taskDefinition1.setDeviationOfTheMainTaskDueDateTimeInDays(5);
        taskDefinition1.setContext("School coordination");
        taskDefinition1.setImportance(Importance.VERY_IMPORTANT);

        var taskDefinition2 = new TaskDefinition();
        taskDefinition2.setName("Ask speakers to pick up goodies");
        taskDefinition2.setContext("School coordination");
        taskDefinition2.setImportance(Importance.NOT_SO_IMPORTANT);

        taskTemplate.setTaskDefinitions(Arrays.asList(taskDefinition1, taskDefinition2));

        Map<String, String> variables = new HashMap<>();
        variables.put("subject", "Git");
        variables.put("school", "Hogeschool Gent");
        variables.put("speakers", "Reinout Claeys, Gert Keldermans");

        var dueDateTimeOfMainTask = LocalDateTime.of(2019, 4, 21, 12, 0);
        var taskTemplateEntry = new TaskTemplateEntry(taskTemplate, variables, null, dueDateTimeOfMainTask);

        // act
        var tasks = taskMapper.mapToNewTask(taskTemplateEntry);

        // assert
        assertThat(tasks, hasSize(2));

        var task1 = tasks.get(0);
        assertThat(task1.getId(), is(notNullValue()));
        assertThat(task1.getName(), is("Ask speaker for a workshop about Git at Hogeschool Gent"));
        assertThat(task1.getStartDateTime(), is(nullValue()));
        assertThat(task1.getDueDateTime(), is(LocalDateTime.of(2019, 4, 26, 12, 0)));
        assertThat(task1.getExpectedDurationInHours(), is(nullValue()));
        assertThat(task1.getContext(), is("School coordination"));
        assertThat(task1.getImportance(), is(Importance.VERY_IMPORTANT));
        assertThat(task1.getDescription(), is(nullValue()));

        var task2 = tasks.get(1);
        assertThat(task2.getId(), is(notNullValue()));
        assertThat(task2.getName(), is("Ask speakers to pick up goodies"));
        assertThat(task2.getStartDateTime(), is(nullValue()));
        assertThat(task2.getDueDateTime(), is(nullValue()));
        assertThat(task2.getExpectedDurationInHours(), is(nullValue()));
        assertThat(task2.getContext(), is("School coordination"));
        assertThat(task2.getImportance(), is(Importance.NOT_SO_IMPORTANT));
        assertThat(task2.getDescription(), is(nullValue()));
    }

    @Test
    public void mapFiringSubscriptionToNewTaskWithMaximalData() {
        // arrange
        var flowId = "flowId";
        var name = "my task";
        var dueDate = LocalDateTime.now();

        var eventData = new HashMap<String, String>();
        eventData.put("task", name);
        eventData.put("maxDueDate", dueDate.toString());

        var event = new Event("Housagotchi", flowId, FlowAction.START, LocalDateTime.now(), eventData);
        var mappingToTask = new SubscriptionMappingToTask("data['task']", "''", "data['maxDueDate']", "'Personal'", "'IMPORTANT'");
        var subscription = new Subscription("subscriptionId", "'Housagotchi'", "'true'", "'false'", mappingToTask);
        var firingSubscription = new FiringSubscription(subscription, event);

        // act
        var result = taskMapper.mapToNewTask(firingSubscription);

        // assert
        assertThat(result.getId(), is(notNullValue()));
        assertThat(result.getFlowId(), is(flowId));
        assertThat(result.getName(), is(name));
        assertThat(result.getCreationDateTime(), is(notNullValue()));
        assertThat(result.getStartDateTime(), is(nullValue()));
        assertThat(result.getDueDateTime(), is(dueDate));
        assertThat(result.getExpectedDurationInHours(), is(nullValue()));
        assertThat(result.getContext(), is("Personal"));
        assertThat(result.getImportance(), is(Importance.IMPORTANT));
        assertThat(result.getDescription(), is(""));
        assertThat(result.getStatus(), is(TaskStatus.OPEN));
        assertThat(result.getHistory(), is(nullValue()));
    }

    @Test
    public void mapFiringSubscriptionToNewTaskWithMinimalData() {
        // arrange
        var flowId = "flowId";
        var name = "my task";

        var eventData = new HashMap<String, String>();
        eventData.put("task", name);

        var event = new Event("Housagotchi", flowId, FlowAction.START, LocalDateTime.now(), eventData);
        var mappingToTask = new SubscriptionMappingToTask("data['task']", null, null, "'Personal'", null);
        var subscription = new Subscription("subscriptionId", "'Housagotchi'", "'true'", "'false'", mappingToTask);
        var firingSubscription = new FiringSubscription(subscription, event);

        // act
        var result = taskMapper.mapToNewTask(firingSubscription);

        // assert
        assertThat(result.getId(), is(notNullValue()));
        assertThat(result.getFlowId(), is(flowId));
        assertThat(result.getName(), is(name));
        assertThat(result.getCreationDateTime(), is(notNullValue()));
        assertThat(result.getStartDateTime(), is(nullValue()));
        assertThat(result.getDueDateTime(), is(nullValue()));
        assertThat(result.getExpectedDurationInHours(), is(nullValue()));
        assertThat(result.getContext(), is("Personal"));
        assertThat(result.getImportance(), is(nullValue()));
        assertThat(result.getDescription(), is(nullValue()));
        assertThat(result.getStatus(), is(TaskStatus.OPEN));
        assertThat(result.getHistory(), is(nullValue()));
    }
}
