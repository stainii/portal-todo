package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.dtos.TaskTemplateEntry;
import be.stijnhooft.portal.todo.model.Importance;
import be.stijnhooft.portal.todo.model.TaskTemplate;
import be.stijnhooft.portal.todo.repositories.TaskTemplateRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

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
        mainTaskTemplate.setExpectedDurationInHours(1);
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

        mainTaskTemplate.getFollowUpTaskTemplates().add(subTaskTemplate1);
        subTaskTemplate1.getFollowUpTaskTemplates().add(subTaskTemplate2);

        Map<String, String> variables = new HashMap<>();
        variables.put("subject", "Git");
        variables.put("school", "Hogeschool Gent");
        variables.put("speakers", "Reinout Claeys, Gert Keldermans");

        var dueDateTimeOfMainTask = LocalDateTime.of(2019, 4, 21, 12, 0);

        var taskTemplateEntry = new TaskTemplateEntry(mainTaskTemplate, variables, null, dueDateTimeOfMainTask);

        // act
        var tasks = taskTemplateService.toTasks(taskTemplateEntry);

        // assert
        var firstTask = tasks.get(0);
        assertThat(firstTask.getName(), is("Arrange workshop about Git for Hogeschool Gent"));
        assertThat(firstTask.getStartDateTime(), is(nullValue()));
        assertThat(firstTask.getDueDateTime(), is(dueDateTimeOfMainTask));
        assertThat(firstTask.getExpectedDurationInHours(), is(1));
        assertThat(firstTask.getContext(), is("School coordination"));
        assertThat(firstTask.getImportance(), is(Importance.VERY_IMPORTANT));
        assertThat(firstTask.getDescription(), is("Hogeschool Gent has asked for a workshop about Git. Possible speakers: Reinout Claeys, Gert Keldermans."));

        var followUpTask1 = tasks.get(1);
        assertThat(followUpTask1.getName(), is("Ask speaker for a workshop about Git at Hogeschool Gent"));
        assertThat(followUpTask1.getStartDateTime(), is(nullValue()));
        assertThat(followUpTask1.getDueDateTime(), is(LocalDateTime.of(2019, 4, 26, 12, 0)));
        assertThat(followUpTask1.getExpectedDurationInHours(), is(nullValue()));
        assertThat(followUpTask1.getContext(), is("School coordination"));
        assertThat(followUpTask1.getImportance(), is(Importance.VERY_IMPORTANT));
        assertThat(followUpTask1.getDescription(), is(nullValue()));

        var followUpTask2 = tasks.get(2);
        assertThat(followUpTask2.getName(), is("Ask speakers to pick up goodies"));
        assertThat(followUpTask2.getStartDateTime(), is(nullValue()));
        assertThat(followUpTask2.getDueDateTime(), is(nullValue()));
        assertThat(followUpTask2.getExpectedDurationInHours(), is(nullValue()));
        assertThat(followUpTask2.getContext(), is("School coordination"));
        assertThat(followUpTask2.getImportance(), is(Importance.NOT_SO_IMPORTANT));
        assertThat(followUpTask2.getDescription(), is(nullValue()));
    }

    @Test
    public void createWithfollowUpTaskTemplates() {
        // arrange
        TaskTemplate taskTemplate = new TaskTemplate();
        taskTemplate.setId("main");

        TaskTemplate subTaskTemplate1 = new TaskTemplate();
        subTaskTemplate1.setId("sub1");

        TaskTemplate subTaskTemplate2 = new TaskTemplate();
        subTaskTemplate2.setId("sub2");

        TaskTemplate subTaskTemplate3 = new TaskTemplate();
        subTaskTemplate3.setId("sub3");

        subTaskTemplate1.setFollowUpTaskTemplates(Arrays.asList(subTaskTemplate2));
        taskTemplate.setFollowUpTaskTemplates(Arrays.asList(subTaskTemplate1, subTaskTemplate3));

        doReturn(taskTemplate).when(taskTemplateRepository).save(taskTemplate);
        doReturn(subTaskTemplate1).when(taskTemplateRepository).save(subTaskTemplate1);
        doReturn(subTaskTemplate2).when(taskTemplateRepository).save(subTaskTemplate2);
        doReturn(subTaskTemplate3).when(taskTemplateRepository).save(subTaskTemplate3);

        // act
        TaskTemplate result = taskTemplateService.create(taskTemplate);

        // assert
        InOrder inOrder = inOrder(taskTemplateRepository);
        inOrder.verify(taskTemplateRepository).save(subTaskTemplate2);
        inOrder.verify(taskTemplateRepository).save(subTaskTemplate1);
        inOrder.verify(taskTemplateRepository).save(subTaskTemplate3);
        inOrder.verify(taskTemplateRepository).save(taskTemplate);
        assertEquals(taskTemplate, result);
    }

    @Test
    public void createWithoutfollowUpTaskTemplates() {
        // arrange
        TaskTemplate taskTemplate = new TaskTemplate();
        taskTemplate.setId("main");

        doReturn(taskTemplate).when(taskTemplateRepository).save(taskTemplate);

        // act
        TaskTemplate result = taskTemplateService.create(taskTemplate);

        // assert
        verify(taskTemplateRepository).save(taskTemplate);
        assertEquals(taskTemplate, result);
    }

    @Test
    public void updateWithfollowUpTaskTemplates() {
        // arrange
        TaskTemplate originalTaskTemplate = new TaskTemplate();
        originalTaskTemplate.setId("main");
        originalTaskTemplate.setName("original");

        TaskTemplate originalSubTaskTemplate1 = new TaskTemplate();
        originalSubTaskTemplate1.setId("sub1");

        TaskTemplate originalSubTaskTemplate2 = new TaskTemplate();
        originalSubTaskTemplate2.setId("sub2");

        TaskTemplate originalSubTaskTemplate3 = new TaskTemplate();
        originalSubTaskTemplate3.setId("sub3");

        originalSubTaskTemplate1.setFollowUpTaskTemplates(Arrays.asList(originalSubTaskTemplate2));
        originalTaskTemplate.setFollowUpTaskTemplates(Arrays.asList(originalSubTaskTemplate1, originalSubTaskTemplate3));


        TaskTemplate updatedTaskTemplate = new TaskTemplate();
        updatedTaskTemplate.setId("main");
        updatedTaskTemplate.setName("updated");

        TaskTemplate updatedSubTaskTemplate1 = new TaskTemplate();
        updatedSubTaskTemplate1.setId("sub1");

        TaskTemplate updatedSubTaskTemplate2 = new TaskTemplate();
        updatedSubTaskTemplate2.setId("sub2");

        updatedTaskTemplate.setFollowUpTaskTemplates(Arrays.asList(updatedSubTaskTemplate1, updatedSubTaskTemplate2));

        doReturn(Optional.of(originalTaskTemplate)).when(taskTemplateRepository).findById("main");
        doReturn(updatedTaskTemplate).when(taskTemplateRepository).save(updatedTaskTemplate);
        doReturn(updatedSubTaskTemplate1).when(taskTemplateRepository).save(updatedSubTaskTemplate1);
        doReturn(updatedSubTaskTemplate2).when(taskTemplateRepository).save(updatedSubTaskTemplate2);

        // act
        TaskTemplate result = taskTemplateService.update(updatedTaskTemplate);

        // assert
        InOrder inOrder = inOrder(taskTemplateRepository);
        inOrder.verify(taskTemplateRepository).findById("main");
        inOrder.verify(taskTemplateRepository).delete(originalSubTaskTemplate2);
        inOrder.verify(taskTemplateRepository).delete(originalSubTaskTemplate1);
        inOrder.verify(taskTemplateRepository).delete(originalSubTaskTemplate3);
        inOrder.verify(taskTemplateRepository).delete(originalTaskTemplate);
        inOrder.verify(taskTemplateRepository).save(updatedSubTaskTemplate1);
        inOrder.verify(taskTemplateRepository).save(updatedSubTaskTemplate2);
        inOrder.verify(taskTemplateRepository).save(updatedTaskTemplate);
        assertEquals(updatedTaskTemplate, result);
    }

    @Test
    public void updateWithoutfollowUpTaskTemplates() {
        // arrange
        TaskTemplate originalTaskTemplate = new TaskTemplate();
        originalTaskTemplate.setId("main");
        originalTaskTemplate.setName("original");

        TaskTemplate updatedTaskTemplate = new TaskTemplate();
        updatedTaskTemplate.setId("main");
        updatedTaskTemplate.setName("updated");

        doReturn(Optional.of(originalTaskTemplate)).when(taskTemplateRepository).findById("main");
        doReturn(updatedTaskTemplate).when(taskTemplateRepository).save(updatedTaskTemplate);

        // act
        TaskTemplate result = taskTemplateService.update(updatedTaskTemplate);

        // assert
        InOrder inOrder = inOrder(taskTemplateRepository);
        inOrder.verify(taskTemplateRepository).delete(originalTaskTemplate);
        inOrder.verify(taskTemplateRepository).save(updatedTaskTemplate);
        assertEquals(updatedTaskTemplate, result);
    }

    @Test
    public void deleteWithfollowUpTaskTemplates() {
        // arrange
        TaskTemplate taskTemplate = new TaskTemplate();
        taskTemplate.setId("main");

        TaskTemplate subTaskTemplate1 = new TaskTemplate();
        subTaskTemplate1.setId("sub1");

        TaskTemplate subTaskTemplate2 = new TaskTemplate();
        subTaskTemplate2.setId("sub2");

        TaskTemplate subTaskTemplate3 = new TaskTemplate();
        subTaskTemplate3.setId("sub3");

        subTaskTemplate1.setFollowUpTaskTemplates(Arrays.asList(subTaskTemplate2));
        taskTemplate.setFollowUpTaskTemplates(Arrays.asList(subTaskTemplate1, subTaskTemplate3));

        // act
        taskTemplateService.delete(taskTemplate);

        // assert
        InOrder inOrder = inOrder(taskTemplateRepository);
        inOrder.verify(taskTemplateRepository).delete(subTaskTemplate2);
        inOrder.verify(taskTemplateRepository).delete(subTaskTemplate1);
        inOrder.verify(taskTemplateRepository).delete(subTaskTemplate3);
        inOrder.verify(taskTemplateRepository).delete(taskTemplate);
    }

    @Test
    public void deleteByIdWithfollowUpTaskTemplates() {
        // arrange
        TaskTemplate taskTemplate = new TaskTemplate();
        taskTemplate.setId("main");

        TaskTemplate subTaskTemplate1 = new TaskTemplate();
        subTaskTemplate1.setId("sub1");

        TaskTemplate subTaskTemplate2 = new TaskTemplate();
        subTaskTemplate2.setId("sub2");

        TaskTemplate subTaskTemplate3 = new TaskTemplate();
        subTaskTemplate3.setId("sub3");

        subTaskTemplate1.setFollowUpTaskTemplates(Arrays.asList(subTaskTemplate2));
        taskTemplate.setFollowUpTaskTemplates(Arrays.asList(subTaskTemplate1, subTaskTemplate3));

        doReturn(Optional.of(taskTemplate)).when(taskTemplateRepository).findById("main");

        // act
        taskTemplateService.delete("main");

        // assert
        InOrder inOrder = inOrder(taskTemplateRepository);
        inOrder.verify(taskTemplateRepository).findById("main");
        inOrder.verify(taskTemplateRepository).delete(subTaskTemplate2);
        inOrder.verify(taskTemplateRepository).delete(subTaskTemplate1);
        inOrder.verify(taskTemplateRepository).delete(subTaskTemplate3);
        inOrder.verify(taskTemplateRepository).delete(taskTemplate);
    }

    @Test
    public void deleteWithoutfollowUpTaskTemplates() {
        // arrange
        TaskTemplate taskTemplate = new TaskTemplate();
        taskTemplate.setId("main");

        // act
        taskTemplateService.delete(taskTemplate);

        // assert
        verify(taskTemplateRepository).delete(taskTemplate);
    }

    @Test
    public void deleteByIdWithoutfollowUpTaskTemplates() {
        // arrange
        TaskTemplate taskTemplate = new TaskTemplate();
        taskTemplate.setId("main");

        doReturn(Optional.of(taskTemplate)).when(taskTemplateRepository).findById("main");

        // act
        taskTemplateService.delete("main");

        // assert
        verify(taskTemplateRepository).findById("main");
        verify(taskTemplateRepository).delete(taskTemplate);
    }
}
