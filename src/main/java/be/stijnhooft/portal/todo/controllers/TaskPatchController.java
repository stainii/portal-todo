package be.stijnhooft.portal.todo.controllers;

import be.stijnhooft.portal.todo.model.TaskPatch;
import be.stijnhooft.portal.todo.services.SecurityService;
import be.stijnhooft.portal.todo.services.TaskPatchSseEmitterService;
import be.stijnhooft.portal.todo.services.TaskPatchService;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.http.HttpResponse;
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
    private final SecurityService securityService;

    public TaskPatchController(TaskPatchService taskPatchService, TaskPatchSseEmitterService taskPatchSseEmitterService, SecurityService securityService) {
        this.taskPatchService = taskPatchService;
        this.taskPatchSseEmitterService = taskPatchSseEmitterService;
        this.securityService = securityService;
    }

    @GetMapping(path = "/", params = "since")
    public List<TaskPatch> getAllTaskPatchesSince(@RequestParam("since") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime) {
        return taskPatchService.findAllTaskPatchesSince(startDateTime);
    }

    /**
     * Required: 2 request parameters:
     * - tail (no value)
     * - jwt (jwt token of logged in user)
     *
     * Why taking the risk of putting the jwt token in the url?
     * The EventSource JavaScript class does not support passing headers,
     * so it's not possible to pass through the Authorization header to this method.
     *
     * @param jwtToken jwtToken of logged in user
     * @return sse emitter
     */
    @GetMapping(path = "/", params = "tail")
    public ResponseEntity<SseEmitter> tail(@RequestParam("jwt") String jwtToken) {
        try {
            securityService.validateJtwToken(jwtToken);
        } catch (JwtException exception) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        return ResponseEntity.ok(taskPatchSseEmitterService.registerListener());
    }

}
