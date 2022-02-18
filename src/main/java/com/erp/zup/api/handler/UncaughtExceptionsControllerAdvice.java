package com.erp.zup.api.handler;

import jflunt.notifications.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class UncaughtExceptionsControllerAdvice  extends ResponseEntityExceptionHandler {
    private static final Logger logger = LogManager.getLogger();
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info("Argument not valid" + ex.getMessage());

        return new ResponseEntity<>(ex.getBindingResult().getAllErrors().stream().map(i -> (FieldError) i)
                .collect(Collectors.toList()).stream().map(i -> new Notification(i.getField(), i.getDefaultMessage()))
                .collect(Collectors.toList()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, null,
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
}