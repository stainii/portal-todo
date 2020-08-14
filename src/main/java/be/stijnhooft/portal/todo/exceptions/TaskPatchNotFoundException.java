package be.stijnhooft.portal.todo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class TaskPatchNotFoundException extends RuntimeException {

    public TaskPatchNotFoundException() {
        super();
    }

    public TaskPatchNotFoundException(String message) {
        super(message);
    }

    public TaskPatchNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
