package com.SocialMediaAPI.advice;

import com.SocialMediaAPI.dto.ApiErrorDto;
import com.SocialMediaAPI.exception.ImageNotFoundException;
import com.SocialMediaAPI.exception.PostNotFoundException;
import com.SocialMediaAPI.exception.UserAlreadyExistException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;
import java.util.List;

@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ApiErrorDto errorDto = new ApiErrorDto();
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        errorDto.setErrors(errors);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { UserAlreadyExistException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<Object> handleConflict(
            RuntimeException ex, WebRequest request) {

        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = {FileSizeLimitExceededException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<Object> handleFileSizeLimit(
            RuntimeException ex, WebRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto();
        errorDto.setErrors(List.of(ex.getMessage() + ", max file size 10MB, max post size 50MB"));
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {ImageNotFoundException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<Object> handleImageNotFound(
            RuntimeException ex, WebRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto();
        errorDto.setErrors(List.of(ex.getMessage()));
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {PostNotFoundException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<Object> handlePostNotFound(
            RuntimeException ex, WebRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto();
        errorDto.setErrors(List.of(ex.getMessage()));
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}
