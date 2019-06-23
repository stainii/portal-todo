package be.stijnhooft.portal.todo.services;

import be.stijnhooft.portal.todo.events.TaskCreated;
import be.stijnhooft.portal.todo.events.TaskEvent;
import be.stijnhooft.portal.todo.events.TaskPatched;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TaskPatchSseEmitterService {

    protected List<SseEmitter> sseEmitters;

    public TaskPatchSseEmitterService() {
        sseEmitters = new ArrayList<>();
    }

    public SseEmitter registerListener() {
        SseEmitter emitter = new SseEmitter();
        this.sseEmitters.add(emitter);

        emitter.onTimeout(() -> {
            emitter.complete();
            this.sseEmitters.remove(emitter);
        });

        return emitter;
    }

    @EventListener
    public void onTaskPatch(TaskPatched taskPatched) {
        onTaskEvent(taskPatched);
    }

    @EventListener
    public void onTaskCreated(TaskCreated taskCreated) {
        onTaskEvent(taskCreated);
    }

    private void onTaskEvent(TaskEvent taskEvent) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        if (sseEmitters.isEmpty()) {
            log.debug("No listening browsers to send the task patch to");
        } else {
            log.debug("Sending the patch to " + sseEmitters.size() + " browsers");
        }
        this.sseEmitters.forEach(emitter -> {
            try {
                emitter.send(taskEvent.getTaskPatch());
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        this.sseEmitters.remove(deadEmitters);
    }
}
