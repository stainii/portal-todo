package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.dtos.TaskTemplateEntry;
import be.stijnhooft.portal.todo.model.Importance;
import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.model.TaskTemplate;
import be.stijnhooft.portal.todo.repositories.TaskTemplateRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

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
        var mainTaskTemplate = new TaskTemplate();
        mainTaskTemplate.setName("Arrange workshop about ${subject} for ${school}");
        mainTaskTemplate.setDeviationOfTheMainTaskStartDateTime(Duration.ZERO);
        mainTaskTemplate.setDeviationOfTheMainTaskDueDateTime(Duration.ZERO);
        mainTaskTemplate.setExpectedDuration(Duration.of(1, ChronoUnit.HOURS));
        mainTaskTemplate.setContext("School coordination");
        mainTaskTemplate.setImportance(Importance.VERY_IMPORTANT);
        mainTaskTemplate.setDescription("${school} has asked for a workshop about ${subject}. Possible speakers: ${speakers}.");
        mainTaskTemplate.setVariableNames(Arrays.asList("subject", "school", "speakers"));

        var subTaskTemplate1 = new TaskTemplate();
        subTaskTemplate1.setName("Ask speaker for a workshop about ${subject} at ${school}");
        subTaskTemplate1.setDeviationOfTheMainTaskStartDateTime(Duration.ZERO);
        subTaskTemplate1.setDeviationOfTheMainTaskDueDateTime(Duration.of(5, ChronoUnit.DAYS));
        subTaskTemplate1.setContext("School coordination");
        subTaskTemplate1.setImportance(Importance.VERY_IMPORTANT);
        subTaskTemplate1.setVariableNames(Arrays.asList("subject", "school", "speakers"));

        var subTaskTemplate2 = new TaskTemplate();
        subTaskTemplate2.setName("Ask speakers to pick up goodies");
        subTaskTemplate2.setContext("School coordination");
        subTaskTemplate2.setImportance(Importance.NOT_SO_IMPORTANT);

        mainTaskTemplate.getSubTaskTemplates().add(subTaskTemplate1);
        subTaskTemplate1.getSubTaskTemplates().add(subTaskTemplate2);

        Map<String, String> variables = new HashMap<>();
        variables.put("subject", "Git");
        variables.put("school", "Hogeschool Gent");
        variables.put("speakers", "Reinout Claeys, Gert Keldermans");

        LocalDateTime dueDateTimeOfMainTask = LocalDateTime.of(2019, 4, 21, 12, 0);

        var taskTemplateEntry = new TaskTemplateEntry(mainTaskTemplate, variables, null, dueDateTimeOfMainTask);

        // act
        Task mainTask = taskTemplateService.toTask(taskTemplateEntry);

        // assert
        assertThat(mainTask.getName(), is("Arrange workshop about Git for Hogeschool Gent"));
        assertThat(mainTask.getStartDateTime(), is(nullValue()));
        assertThat(mainTask.getDueDateTime(), is(dueDateTimeOfMainTask));
        assertThat(mainTask.getExpectedDuration(), is(Duration.of(1, ChronoUnit.HOURS)));
        assertThat(mainTask.getContext(), is("School coordination"));
        assertThat(mainTask.getImportance(), is(Importance.VERY_IMPORTANT));
        assertThat(mainTask.getDescription(), is("Hogeschool Gent has asked for a workshop about Git. Possible speakers: Reinout Claeys, Gert Keldermans."));
        assertThat("size of subtasks of main task", mainTask.getSubTasks().size(), is(1));

        Task subTask1 = mainTask.getSubTasks().get(0);
        assertThat(subTask1.getName(), is("Ask speaker for a workshop about Git at Hogeschool Gent"));
        assertThat(subTask1.getStartDateTime(), is(nullValue()));
        assertThat(subTask1.getDueDateTime(), is(LocalDateTime.of(2019, 4, 26, 12, 0)));
        assertThat(subTask1.getExpectedDuration(), is(nullValue()));
        assertThat(subTask1.getContext(), is("School coordination"));
        assertThat(subTask1.getImportance(), is(Importance.VERY_IMPORTANT));
        assertThat(subTask1.getDescription(), is(nullValue()));
        assertThat("size of subtasks of sub task 1", subTask1.getSubTasks().size(), is(1));

        Task subTask2 = subTask1.getSubTasks().get(0);
        assertThat(subTask2.getName(), is("Ask speakers to pick up goodies"));
        assertThat(subTask2.getStartDateTime(), is(nullValue()));
        assertThat(subTask2.getDueDateTime(), is(nullValue()));
        assertThat(subTask2.getExpectedDuration(), is(nullValue()));
        assertThat(subTask2.getContext(), is("School coordination"));
        assertThat(subTask2.getImportance(), is(Importance.NOT_SO_IMPORTANT));
        assertThat(subTask2.getDescription(), is(nullValue()));
        assertThat(subTask2.getSubTasks(), is(empty()));
    }
}
