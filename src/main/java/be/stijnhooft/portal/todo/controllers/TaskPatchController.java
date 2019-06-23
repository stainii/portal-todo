package be.stijnhooft.portal.todo.controllers;

import be.stijnhooft.portal.todo.model.TaskPatch;
import be.stijnhooft.portal.todo.services.TaskPatchSseEmitterService;
import be.stijnhooft.portal.todo.services.TaskPatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;

// TODO: find a way to write an integration test for server side events
// TODO: document
@RestController
@RequestMapping("/task/patch/")
@Slf4j
public class TaskPatchController {

    private final TaskPatchService taskPatchService;
    private final TaskPatchSseEmitterService taskPatchSseEmitterService;

    public TaskPatchController(TaskPatchService taskPatchService, TaskPatchSseEmitterService taskPatchSseEmitterService) {
        this.taskPatchService = taskPatchService;
        this.taskPatchSseEmitterService = taskPatchSseEmitterService;
    }

    @GetMapping(path = "/", params = "since")
    public List<TaskPatch> getAllTaskPatchesSince(@RequestParam("since") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime) {
        return taskPatchService.findAllTaskPatchesSince(startDateTime);
    }

    @GetMapping(path = "/", params = "tail")
    public SseEmitter tail() {
        return taskPatchSseEmitterService.registerListener();
    }

}
