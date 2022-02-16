package com.erp.zup.api;

import jflunt.notifications.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class ExceptionController extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {


        return new ResponseEntity<>(ex.getBindingResult().getAllErrors().stream().map(i -> (FieldError)i)
                .collect(Collectors.toList()).stream().map(i -> new Notification(i.getField(), i.getDefaultMessage()))
                .collect(Collectors.toList()), HttpStatus.BAD_REQUEST);
    }
}