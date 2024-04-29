package com.oleksiity.usersapi.controller.heandlers;

import com.oleksiity.usersapi.exception.InvalidDateRangeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class BasicExceptionHandler {

    private final MessageSource messageSource;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(
            MethodArgumentNotValidException ex, Locale locale) {
        log.error("MethodArgumentNotValidException exception has been handled. Exception details", ex);
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                Objects.requireNonNull(messageSource.getMessage("errors.400.title", new Object[0],
                        "errors.400.title", locale)));
        problemDetail.setProperty("errors", ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .toList());
        return problemDetail;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail handleNoSuchElementException(
            NoSuchElementException ex, Locale locale) {
        log.error("NoSuchElementException exception has been handled. Exception details", ex);
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                Objects.requireNonNull(messageSource.getMessage("errors.404.title", new Object[0],
                        "errors.404.title", locale)));

        problemDetail.setProperty("errors", Collections.singletonList(
                messageSource.getMessage(ex.getMessage(), new Object[0], ex.getMessage(), locale)));
        return problemDetail;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidDateRangeException.class)
    public ProblemDetail handleDateRangeExceptions(
            InvalidDateRangeException ex, Locale locale) {
        log.error("InvalidDateRangeException exception has been handled. Exception details", ex);
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                Objects.requireNonNull(messageSource.getMessage("errors.400.title", new Object[0],
                        "errors.400.title", locale)));
        problemDetail.setProperty("errors", Collections.singletonList(
                messageSource.getMessage(ex.getMessage(), new Object[0], ex.getMessage(), locale)));
        return problemDetail;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleServerExceptions(
            InvalidDateRangeException ex, Locale locale) {
        log.error("Unexpected exception has been handled. Exception details", ex);
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                Objects.requireNonNull(messageSource.getMessage("errors.500.title", new Object[0],
                        "errors.500.title", locale)));
        problemDetail.setProperty("errors", Collections.singletonList(ex.getMessage()));
        return problemDetail;
    }
}
