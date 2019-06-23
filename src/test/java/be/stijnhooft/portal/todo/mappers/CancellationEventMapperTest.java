package be.stijnhooft.portal.todo.mappers;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.model.TaskPatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static be.stijnhooft.portal.todo.PortalTodoApplication.APPLICATION_NAME;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CancellationEventMapperTest {

    @InjectMocks
    private CancellationEventMapper cancellationEventMapper;

    @Test(expected = NullPointerException.class)
    public void mapWhenTaskIsNull() {
        cancellationEventMapper.map(null);
    }

    @Test
    public void mapWhenSuccess() {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setTaskId("12");

        Event event = cancellationEventMapper.map(taskPatch);
        assertThat(event.getSource(), is(APPLICATION_NAME));
        assertThat(event.getFlowId(), is(APPLICATION_NAME + "-12"));
        assertThat(event.getPublishDate(), is(notNullValue()));
        assertThat(event.getData().keySet(), hasItem("type"));
        assertThat(event.getData().get("type"), is("cancellation"));
    }

}
