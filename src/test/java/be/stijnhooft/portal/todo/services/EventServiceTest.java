package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.dtos.FiringSubscription;
import be.stijnhooft.portal.todo.mappers.TaskMapper;
import be.stijnhooft.portal.todo.mappers.TaskPatchMapper;
import be.stijnhooft.portal.todo.model.Importance;
import be.stijnhooft.portal.todo.model.subscription.Subscription;
import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import be.stijnhooft.portal.todo.model.task.TaskStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    private Clock clock = Clock.fixed(ZonedDateTime.of(2019, 10, 10, 10, 10, 10, 10, ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());;

    @Mock
    private TaskService taskService;

    @Mock
    private TaskPatchService taskPatchService;

    @Mock
    private TaskPatchMapper taskPatchMapper;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private SubscriptionService subscriptionService;

    @Test
    public void receiveEventsWithOnlyCreationEvents() {
        //data set
        var event1 = new Event("source1", "flow1", LocalDateTime.now(), new HashMap<>());
        var event2 = new Event("source2", "flow2", LocalDateTime.now(), new HashMap<>());
        var event3 = new Event("source3", "flow3", LocalDateTime.now(), new HashMap<>());

        var firingSubscription1 = new FiringSubscription(new Subscription(), event1);
        var firingSubscription2 = new FiringSubscription(new Subscription(), event3);

        var task1 = new Task("flow1", "name", clock.instant(), null, LocalDateTime.now(), null, "context", Importance.IMPORTANT, "description", TaskStatus.OPEN, null);
        var task2 = new Task("flow3", "name", clock.instant(), null, LocalDateTime.now(), null, "context", Importance.IMPORTANT, "description", TaskStatus.OPEN, null);

        //mock
        doReturn(Stream.of(firingSubscription1)).when(subscriptionService).fireOnCreationCondition(event1);
        doReturn(Stream.empty()).when(subscriptionService).fireOnCreationCondition(event2);
        doReturn(Stream.of(firingSubscription2)).when(subscriptionService).fireOnCreationCondition(event3);

        doReturn(task1).when(taskMapper).map(firingSubscription1);
        doReturn(task2).when(taskMapper).map(firingSubscription2);

        doReturn(Stream.empty()).when(subscriptionService).fireOnCompleteCondition(event1);
        doReturn(Stream.empty()).when(subscriptionService).fireOnCompleteCondition(event2);
        doReturn(Stream.empty()).when(subscriptionService).fireOnCompleteCondition(event3);

        //execute
        eventService.receiveEvents(Arrays.asList(event1, event2, event3));

        //verify
        verify(subscriptionService).fireOnCreationCondition(event1);
        verify(subscriptionService).fireOnCreationCondition(event2);
        verify(subscriptionService).fireOnCreationCondition(event3);

        verify(taskMapper).map(firingSubscription1);
        verify(taskMapper).map(firingSubscription2);

        verify(taskService).create(Arrays.asList(task1, task2));

        verify(subscriptionService).fireOnCompleteCondition(event1);
        verify(subscriptionService).fireOnCompleteCondition(event2);
        verify(subscriptionService).fireOnCompleteCondition(event3);

        verifyNoMoreInteractions(subscriptionService, taskMapper, taskService, taskPatchMapper, taskPatchService);
    }

    @Test
    public void receiveEventsWithOnlyCompleteEvents() {
        //data set
        var event1 = new Event("source1", "flow1", LocalDateTime.now(), new HashMap<>());
        var event2 = new Event("source2", "flow2", LocalDateTime.now(), new HashMap<>());
        var event3 = new Event("source3", "flow3", LocalDateTime.now(), new HashMap<>());

        var firingSubscription1 = new FiringSubscription(new Subscription(), event1);
        var firingSubscription2 = new FiringSubscription(new Subscription(), event3);

        var task1 = new Task("flow1", "name", clock.instant(), null, LocalDateTime.now(), null, "context", Importance.IMPORTANT, "description", TaskStatus.OPEN, null);
        var task2 = new Task("flow3", "name", clock.instant(), null, LocalDateTime.now(), null, "context", Importance.IMPORTANT, "description", TaskStatus.OPEN, null);

        var patch1 = new TaskPatch();
        patch1.setTaskId("flow1");
        var patch2 = new TaskPatch();
        patch2.setTaskId("flow3");

        //mock
        doReturn(Stream.empty()).when(subscriptionService).fireOnCreationCondition(event1);
        doReturn(Stream.empty()).when(subscriptionService).fireOnCreationCondition(event2);
        doReturn(Stream.empty()).when(subscriptionService).fireOnCreationCondition(event3);

        doReturn(task1).when(taskMapper).map(firingSubscription1);
        doReturn(task2).when(taskMapper).map(firingSubscription2);

        doReturn(Stream.of(firingSubscription1)).when(subscriptionService).fireOnCompleteCondition(event1);
        doReturn(Stream.empty()).when(subscriptionService).fireOnCompleteCondition(event2);
        doReturn(Stream.of(firingSubscription2)).when(subscriptionService).fireOnCompleteCondition(event3);

        doReturn(patch1).when(taskPatchMapper).from(event1);
        doReturn(patch2).when(taskPatchMapper).from(event3);

        //execute
        eventService.receiveEvents(Arrays.asList(event1, event2, event3));

        //verify
        verify(subscriptionService).fireOnCreationCondition(event1);
        verify(subscriptionService).fireOnCreationCondition(event2);
        verify(subscriptionService).fireOnCreationCondition(event3);

        verify(subscriptionService).fireOnCompleteCondition(event1);
        verify(subscriptionService).fireOnCompleteCondition(event2);
        verify(subscriptionService).fireOnCompleteCondition(event3);

        verify(taskPatchMapper).from(event1);
        verify(taskPatchMapper).from(event3);

        verify(taskPatchService).patch(patch1);
        verify(taskPatchService).patch(patch2);

        verifyNoMoreInteractions(subscriptionService, taskMapper, taskService, taskPatchMapper, taskPatchService);
    }

    @Test
    public void receiveEventsWithAllEvents() {
        //data set
        var event1 = new Event("source1", "flow1", LocalDateTime.now(), new HashMap<>());
        var event2 = new Event("source2", "flow2", LocalDateTime.now(), new HashMap<>());
        var event3 = new Event("source3", "flow3", LocalDateTime.now(), new HashMap<>());

        var firingSubscription1 = new FiringSubscription(new Subscription(), event1);
        var firingSubscription2 = new FiringSubscription(new Subscription(), event3);

        var task1 = new Task("flow1", "name", clock.instant(), null, LocalDateTime.now(), null, "context", Importance.IMPORTANT, "description", TaskStatus.OPEN, null);

        var patch1 = new TaskPatch();
        patch1.setTaskId("flow1");

        //mock
        doReturn(Stream.of(firingSubscription1)).when(subscriptionService).fireOnCreationCondition(event1);
        doReturn(Stream.empty()).when(subscriptionService).fireOnCreationCondition(event2);
        doReturn(Stream.empty()).when(subscriptionService).fireOnCreationCondition(event3);

        doReturn(task1).when(taskMapper).map(firingSubscription1);

        doReturn(Stream.empty()).when(subscriptionService).fireOnCompleteCondition(event1);
        doReturn(Stream.empty()).when(subscriptionService).fireOnCompleteCondition(event2);
        doReturn(Stream.of(firingSubscription2)).when(subscriptionService).fireOnCompleteCondition(event3);

        doReturn(patch1).when(taskPatchMapper).from(event3);

        //execute
        eventService.receiveEvents(Arrays.asList(event1, event2, event3));

        //verify
        verify(subscriptionService).fireOnCreationCondition(event1);
        verify(subscriptionService).fireOnCreationCondition(event2);
        verify(subscriptionService).fireOnCreationCondition(event3);

        verify(taskMapper).map(firingSubscription1);

        verify(taskService).create(List.of(task1));

        verify(subscriptionService).fireOnCompleteCondition(event1);
        verify(subscriptionService).fireOnCompleteCondition(event2);
        verify(subscriptionService).fireOnCompleteCondition(event3);

        verify(taskPatchMapper).from(event3);

        verify(taskPatchService).patch(patch1);

        verifyNoMoreInteractions(subscriptionService, taskMapper, taskService, taskPatchService, taskPatchMapper);
    }

    @Test
    public void receiveEventsButNothingShouldFire() {
        //data set
        var event1 = new Event("source1", "flow1", LocalDateTime.now(), new HashMap<>());
        var event2 = new Event("source2", "flow2", LocalDateTime.now(), new HashMap<>());
        var event3 = new Event("source3", "flow3", LocalDateTime.now(), new HashMap<>());

        //mock
        doReturn(Stream.empty()).when(subscriptionService).fireOnCreationCondition(event1);
        doReturn(Stream.empty()).when(subscriptionService).fireOnCreationCondition(event2);
        doReturn(Stream.empty()).when(subscriptionService).fireOnCreationCondition(event3);

        doReturn(Stream.empty()).when(subscriptionService).fireOnCompleteCondition(event1);
        doReturn(Stream.empty()).when(subscriptionService).fireOnCompleteCondition(event2);
        doReturn(Stream.empty()).when(subscriptionService).fireOnCompleteCondition(event3);

        //execute
        eventService.receiveEvents(Arrays.asList(event1, event2, event3));

        //verify
        verify(subscriptionService).fireOnCreationCondition(event1);
        verify(subscriptionService).fireOnCreationCondition(event2);
        verify(subscriptionService).fireOnCreationCondition(event3);

        verify(subscriptionService).fireOnCompleteCondition(event1);
        verify(subscriptionService).fireOnCompleteCondition(event2);
        verify(subscriptionService).fireOnCompleteCondition(event3);

        verifyNoMoreInteractions(subscriptionService, taskMapper, taskService, taskPatchService, taskPatchMapper);
    }
}
