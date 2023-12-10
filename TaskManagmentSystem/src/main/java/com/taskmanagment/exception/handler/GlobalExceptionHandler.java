package com.taskmanagment.exception.handler;

import com.taskmanagment.exception.NotFoundException;
import com.taskmanagment.exception.NotFoundKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {NotFoundException.class, NotFoundKeyException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ExceptionMessage handlerOnNotFoundException(Exception exception) {
        return getInfoAboutMessage(exception);
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final ExceptionMessage handlerOnAllException(Exception exception) {
        return getInfoAboutMessage(exception);
    }

    private ExceptionMessage getInfoAboutMessage(Exception exception) {
        return new ExceptionMessage(exception.getMessage(), exception.getClass().getName());
    }
}