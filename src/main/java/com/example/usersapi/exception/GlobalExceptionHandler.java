package com.example.usersapi.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        Body body = new Body();
        body.timeStamp = LocalDateTime.now();
        body.status = HttpStatus.BAD_REQUEST;
        body.errors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .toList();
        return new ResponseEntity<>(body, headers, status);
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError) {
            return ((FieldError) e).getField()
                    + " "
                    + e.getDefaultMessage();
        }
        return e.getDefaultMessage();
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(
            MissingPathVariableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        Body body = new Body();
        body.timeStamp = LocalDateTime.now();
        body.status = HttpStatus.BAD_REQUEST;
        body.errors = List.of(ex.getMessage());
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler({
            DateTimeParseException.class,
            RegistrationException.class,
            DatesOrderException.class
    })
    public ResponseEntity<Object> handleBadRequestExceptions(
            Exception ex
    ) {
        Body body = new Body();
        body.timeStamp = LocalDateTime.now();
        body.status = HttpStatus.BAD_REQUEST;
        body.errors = List.of(ex.getMessage());
        return new ResponseEntity<>(body, body.status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(
            Exception ex
    ) {
        Body body = new Body();
        body.timeStamp = LocalDateTime.now();
        body.status = HttpStatus.NOT_FOUND;
        body.errors = List.of(ex.getMessage());
        return new ResponseEntity<>(body, body.status);
    }

    @Data
    private static class Body {
        private List<String> errors;
        private LocalDateTime timeStamp;
        private HttpStatus status;
    }
}
