package com.SocialMediaAPI.advice;

import com.SocialMediaAPI.dto.ApiErrorDto;
import com.SocialMediaAPI.dto.ErrorDto;
import com.SocialMediaAPI.exception.*;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;
import java.util.List;

@RestControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ErrorDto> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(objectError -> new ErrorDto(ex.getObjectName(), objectError.getDefaultMessage()))
                .collect(Collectors.toList());
        ApiErrorDto errorDto = new ApiErrorDto(HttpStatus.BAD_REQUEST,
                ((ServletWebRequest)request).getRequest().getRequestURI().toString(),
                errors.toArray(new ErrorDto[0])
        );
        return handleExceptionInternal(ex, errorDto, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto(HttpStatus.BAD_REQUEST,
                ((ServletWebRequest)request).getRequest().getRequestURI().toString(),
                new ErrorDto(ex.getClass().getName(), "Required parameter: '" + ex.getParameterName() + "' is missing!")
        );
        return handleExceptionInternal(ex, errorDto, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto(HttpStatus.BAD_REQUEST,
                ((ServletWebRequest)request).getRequest().getRequestURI().toString(),
                new ErrorDto(ex.getClass().getName(), "Required parameter: '" + ex.getRequestPartName() + "' is missing!")
        );
        return handleExceptionInternal(ex, errorDto, headers, HttpStatus.BAD_REQUEST, request);
    }


    @ExceptionHandler(value = {
            UserAlreadyExistException.class,
            EmailAlreadyExistException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<Object> handleConflict(
            RuntimeException ex, WebRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto(HttpStatus.BAD_REQUEST,
                ((ServletWebRequest)request).getRequest().getRequestURI().toString(),
                new ErrorDto(ex.getClass().getName(), ex.getMessage())
        );
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = {FileSizeLimitExceededException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<Object> handleFileSizeLimit(
            RuntimeException ex, WebRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto(HttpStatus.CONFLICT,
                ((ServletWebRequest)request).getRequest().getRequestURI().toString(),
                new ErrorDto(ex.getClass().getName(), ex.getMessage() + ", max file size 10MB, max post size 50MB")
        );
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = {
            ImageNotFoundException.class,
            PostNotFoundException.class,
            UsernameNotFoundException.class,
            NotificationNotFoundEcxeption.class,
            ChatNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<Object> handleNotFound(
            RuntimeException ex, WebRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto(HttpStatus.NOT_FOUND,
                ((ServletWebRequest)request).getRequest().getRequestURI().toString(),
                new ErrorDto(ex.getClass().getName(), ex.getMessage())
        );
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {
            IncompatibleTypeError.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleIncompatibleType(
            RuntimeException ex, WebRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto(HttpStatus.BAD_REQUEST,
                ((ServletWebRequest)request).getRequest().getRequestURI().toString(),
                new ErrorDto(ex.getClass().getName(), ex.getMessage())
        );
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

   /* @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto(HttpStatus.BAD_REQUEST,
                ((ServletWebRequest)request).getRequest().getRequestURI().toString(),
                new ErrorDto(ex.getClass().getName(), "Required body param missing!")
        );
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }*/

    @ExceptionHandler(value = {
            InvalidTokenRequestException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<Object> handleInvalidToken(
            RuntimeException ex, WebRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto(HttpStatus.UNAUTHORIZED,
                ((ServletWebRequest)request).getRequest().getRequestURI().toString(),
                new ErrorDto(ex.getClass().getName(), ex.getMessage())
        );
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(value = {
            UserLoginException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<Object> handleLoginBadCredentials(
            RuntimeException ex, WebRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto(HttpStatus.UNAUTHORIZED,
                ((ServletWebRequest)request).getRequest().getRequestURI().toString(),
                new ErrorDto(ex.getClass().getName(), "Bad credentials! Username or Password")
        );
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto(HttpStatus.NOT_FOUND,
                ((ServletWebRequest)request).getRequest().getRequestURI().toString(),
                new ErrorDto(ex.getClass().getName(), ex.getMessage())
        );
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto(HttpStatus.METHOD_NOT_ALLOWED,
                ((ServletWebRequest)request).getRequest().getRequestURI().toString(),
                new ErrorDto(ex.getClass().getName(), ex.getMessage())
        );
        return handleExceptionInternal(ex, errorDto, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED, request);
    }
}
