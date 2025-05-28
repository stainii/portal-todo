package be.stijnhooft.portal.todo.controllers;

import be.stijnhooft.portal.todo.exceptions.TaskNotFoundException;
import be.stijnhooft.portal.todo.model.task.TaskPatch;
import be.stijnhooft.portal.todo.model.task.TaskPatchResult;
import be.stijnhooft.portal.todo.services.TaskPatchService;
import be.stijnhooft.portal.todo.services.TaskPatchSseEmitterService;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.List;

// TODO: find a way to write an integration test for server side events
@RestController
@RequestMapping("api/task/patch/")
@Slf4j
public class TaskPatchController {

    private final TaskPatchService taskPatchService;
    private final TaskPatchSseEmitterService taskPatchSseEmitterService;

    public TaskPatchController(TaskPatchService taskPatchService, TaskPatchSseEmitterService taskPatchSseEmitterService) {
        this.taskPatchService = taskPatchService;
        this.taskPatchSseEmitterService = taskPatchSseEmitterService;
    }

    @GetMapping(params = "since")
    public List<TaskPatch> getAllTaskPatchesSince(@RequestParam("since") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDateTime) {
        return taskPatchService.findAllTaskPatchesSince(startDateTime);
    }

    /// TODO: add security to this endpoint
    /// Required: 2 request parameters:
    /// - tail (no value)
    /// - jwt (jwt token of logged in user)
    ///
    /// Why taking the risk of putting the jwt token in the url?
    /// The EventSource JavaScript class does not support passing headers,
    /// so it's not possible to pass through the Authorization header to this method.
    ///
    /// @param jwtToken jwtToken of logged in user
    /// @return sse emitter
    @GetMapping(params = "tail")
    public ResponseEntity<SseEmitter> tail(@RequestParam("jwt") String jwtToken) {
        /*try {
            securityService.validateJtwToken(jwtToken);
        } catch (JwtException exception) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }*/

        return ResponseEntity.ok(taskPatchSseEmitterService.registerListener());
    }

    @DeleteMapping("/{id}")
    public TaskPatchResult undoPatch(@PathVariable String id) {
        TaskPatch taskPatch = taskPatchService.findPatchById(id)
            .orElseThrow(TaskNotFoundException::new);
        return taskPatchService.undoPatch(taskPatch);
    }


}
