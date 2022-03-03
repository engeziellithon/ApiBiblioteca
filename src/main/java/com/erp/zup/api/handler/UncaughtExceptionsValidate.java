package com.erp.zup.api.handler;

import com.google.gson.Gson;
import io.sentry.Sentry;
import io.sentry.protocol.User;
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

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class UncaughtExceptionsValidate extends ResponseEntityExceptionHandler  {
    private static final Logger logger = LogManager.getLogger();

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request)  {
        List<Notification> messages = ex.getBindingResult().getAllErrors().stream().map(i -> (FieldError) i)
                .collect(Collectors.toList()).stream().map(i -> new Notification(i.getField(), i.getDefaultMessage()))
                .collect(Collectors.toList());

        User user = new User();
        user.setEmail(request.getRemoteUser());
        Sentry.setUser(user);

        logger.info("Locale: " +  request.getLocale());
        logger.info("Response: " + new Gson().toJson(messages));
        logger.error("Exception",ex);


        return new ResponseEntity<>(messages, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {

        logger.error("Method handle Conflict:",ex);

        return handleExceptionInternal(ex, List.of(new Notification("", "Parâmetros incorretos")),
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
}