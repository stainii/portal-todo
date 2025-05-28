package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.events.TaskCreated;
import be.stijnhooft.portal.todo.events.TaskPatched;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.verify;

public class TaskPatchSseEmitterServiceTest {

    private AutoCloseable mocks;

    @Mock
    private SseEmitter sseEmitter;

    private TaskPatchSseEmitterService taskPatchSseEmitterService;

    @BeforeEach
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        taskPatchSseEmitterService = new TaskPatchSseEmitterService();
        taskPatchSseEmitterService.sseEmitters.add(sseEmitter);
    }

    @Test
    public void registerListener() {
        assertThat(taskPatchSseEmitterService.sseEmitters, hasSize(1));

        SseEmitter sseEmitter = taskPatchSseEmitterService.registerListener();

        assertThat(sseEmitter, is(notNullValue()));
        assertThat(taskPatchSseEmitterService.sseEmitters, hasItem(sseEmitter));
        assertThat(taskPatchSseEmitterService.sseEmitters, hasSize(2));
    }

    @Test
    public void onTaskPatch() throws IOException {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setId("100");

        taskPatchSseEmitterService.onTaskPatch(new TaskPatched(taskPatch));

        verify(sseEmitter).send(taskPatch);
    }

    @Test
    public void onTaskCreated() throws IOException {
        TaskPatch taskPatch = new TaskPatch();
        taskPatch.setId("100");

        taskPatchSseEmitterService.onTaskCreated(new TaskCreated(taskPatch));

        verify(sseEmitter).send(taskPatch);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }
}
