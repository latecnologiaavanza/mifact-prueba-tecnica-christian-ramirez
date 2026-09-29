package com.mifact.inventory.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception, WebRequest request) {
        ApiError error = error(HttpStatus.BAD_REQUEST, "La solicitud contiene datos inválidos", request);
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError ->
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        error.setFieldErrors(fieldErrors);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException exception, WebRequest request) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiError> handleMalformedRequest(Exception exception, WebRequest request) {
        return response(HttpStatus.BAD_REQUEST, "La solicitud no tiene un formato válido", request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException exception, WebRequest request) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException exception, WebRequest request) {
        return response(HttpStatus.CONFLICT, "No se puede guardar el producto porque viola una restricción de datos", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception exception, WebRequest request) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno en el servidor", request);
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String message, WebRequest request) {
        return ResponseEntity.status(status).body(error(status, message, request));
    }

    private ApiError error(HttpStatus status, String message, WebRequest request) {
        String path = request instanceof ServletWebRequest
                ? ((ServletWebRequest) request).getRequest().getRequestURI()
                : request.getDescription(false);
        return new ApiError(status.value(), status.getReasonPhrase(), message, path);
    }
}
