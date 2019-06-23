package be.stijnhooft.portal.todo.controllers;

import be.stijnhooft.portal.todo.events.TaskCreated;
import be.stijnhooft.portal.todo.events.TaskEvent;
import be.stijnhooft.portal.todo.events.TaskPatched;
import be.stijnhooft.portal.todo.model.TaskPatch;
import be.stijnhooft.portal.todo.services.TaskPatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// TODO: find a way to write an integration test for server side events
// TODO: document
@RestController
@RequestMapping("/task/patch/")
@Slf4j
public class TaskPatchController {

    private final TaskPatchService taskPatchService;
    private List<SseEmitter> sseEmitters;

    public TaskPatchController(TaskPatchService taskPatchService) {
        this.taskPatchService = taskPatchService;
        sseEmitters = new ArrayList<>();
    }

    @GetMapping(path = "/", params = "since")
    public List<TaskPatch> getAllTaskPatchesSince(@RequestParam("since") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime) {
        return taskPatchService.findAllTaskPatchesSince(startDateTime);
    }

    @GetMapping(path = "/", params = "tail")
    public SseEmitter tail() {
        SseEmitter emitter = new SseEmitter();
        this.sseEmitters.add(emitter);

        emitter.onTimeout(() -> {
            emitter.complete();
            this.sseEmitters.remove(emitter);
        });

        return emitter;
    }


    private void registerSseEmitterForFutureTaskPatches(SseEmitter emitter) {

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
