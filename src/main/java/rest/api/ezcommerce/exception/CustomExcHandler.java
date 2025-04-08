package rest.api.ezcommerce.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import rest.api.ezcommerce.model.WebResponse;

@RestControllerAdvice
public class CustomExcHandler {
    @ExceptionHandler
    public ResponseEntity<WebResponse<String>> constraintViolationException(ConstraintViolationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(WebResponse.<String>builder()
                                            .status(false)
                                            .errors(exception.getMessage())
                                            .build());
    }

    @ExceptionHandler
    public ResponseEntity<WebResponse<String>> apiException(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(WebResponse.<String>builder()
                                            .status(false)
                                            .errors(exception.getReason())
                                            .build());
    }

    @ExceptionHandler
    public ResponseEntity<WebResponse<String>> handlerNotFoundException(NoHandlerFoundException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(WebResponse.<String>builder()
                                            .status(false)
                                            .errors(exception.getMessage())
                                            .build());
    }
}
