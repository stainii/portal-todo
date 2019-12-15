package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.dtos.TaskTemplateEntry;
import be.stijnhooft.portal.todo.model.task.Importance;
import be.stijnhooft.portal.todo.model.template.TaskDefinition;
import be.stijnhooft.portal.todo.model.template.TaskTemplate;
import be.stijnhooft.portal.todo.repositories.TaskTemplateRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TaskTemplateServiceTest {

    @Mock
    private TaskTemplateRepository taskTemplateRepository;

    private Clock clock = Clock.fixed(ZonedDateTime.of(2019, 10, 10, 10, 10, 10, 10, ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

    private TaskTemplateService taskTemplateService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        taskTemplateService = new TaskTemplateService(taskTemplateRepository, clock);
    }

    @Test
    public void toTask() {
        // arrange
        var taskTemplate = new TaskTemplate();
        taskTemplate.setName("Organize a workshop");
        taskTemplate.setVariableNames(Arrays.asList("subject", "school", "speakers"));

        var taskDefinition1 = new TaskDefinition();
        taskDefinition1.setName("Ask speaker for a workshop about ${subject} at ${school}");
        taskDefinition1.setDeviationOfTheMainTaskStartDateTime(Duration.ZERO);
        taskDefinition1.setDeviationOfTheMainTaskDueDateTime(Duration.of(5, ChronoUnit.DAYS));
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
        var tasks = taskTemplateService.toTasks(taskTemplateEntry);

        // assert
        assertThat(tasks, hasSize(2));

        var task1 = tasks.get(0);
        assertThat(task1.getName(), is("Ask speaker for a workshop about Git at Hogeschool Gent"));
        assertThat(task1.getStartDateTime(), is(nullValue()));
        assertThat(task1.getDueDateTime(), is(LocalDateTime.of(2019, 4, 26, 12, 0)));
        assertThat(task1.getExpectedDurationInHours(), is(nullValue()));
        assertThat(task1.getContext(), is("School coordination"));
        assertThat(task1.getImportance(), is(Importance.VERY_IMPORTANT));
        assertThat(task1.getDescription(), is(nullValue()));

        var task2 = tasks.get(1);
        assertThat(task2.getName(), is("Ask speakers to pick up goodies"));
        assertThat(task2.getStartDateTime(), is(nullValue()));
        assertThat(task2.getDueDateTime(), is(nullValue()));
        assertThat(task2.getExpectedDurationInHours(), is(nullValue()));
        assertThat(task2.getContext(), is("School coordination"));
        assertThat(task2.getImportance(), is(Importance.NOT_SO_IMPORTANT));
        assertThat(task2.getDescription(), is(nullValue()));
    }

}
