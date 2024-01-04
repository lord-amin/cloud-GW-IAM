package com.tiddev.apigateway.conrtoller;

import com.tiddev.apigateway.ratelimit.TooManyRequestsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
            = {TooManyRequestsException.class})
    protected ResponseEntity<Object> handleConflict(
            TooManyRequestsException ex) {
        log.error("", ex);
        return new ResponseEntity<>(Map.of("msg", "Too many requests"), HttpStatus.TOO_MANY_REQUESTS);
    }
}