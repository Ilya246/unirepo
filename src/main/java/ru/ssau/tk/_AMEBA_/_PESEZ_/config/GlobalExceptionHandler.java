package ru.ssau.tk._AMEBA_._PESEZ_.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex, WebRequest request) {
        Log.info("Got error from user request:", ex);

        return new ResponseEntity<>(getErrorInitMessage(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        return new ResponseEntity<>(getErrorInitMessage(ex), HttpStatus.FORBIDDEN);
    }
}