package be.stijnhooft.portal.todo.mappers;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static be.stijnhooft.portal.todo.PortalTodoApplication.APPLICATION_NAME;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
public class CancellationEventMapperTest {

    @InjectMocks
    private CancellationEventMapper cancellationEventMapper;

    @Test
    public void mapWhenTaskIsNull() {
        assertThrows(NullPointerException.class, () ->
                cancellationEventMapper.map(null));
    }

    @Test
    public void mapWhenSuccess() {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setTaskId("12");
        taskPatch.setFlowId(APPLICATION_NAME + "-12");

        Event event = cancellationEventMapper.map(taskPatch);
        assertThat(event.getSource(), is(APPLICATION_NAME));
        assertThat(event.getFlowId(), is(APPLICATION_NAME + "-12"));
        assertThat(event.getPublishDate(), is(notNullValue()));
        assertThat(event.getData().keySet(), hasItem("type"));
        assertThat(event.getData().get("type"), is("cancellation"));
    }

}
