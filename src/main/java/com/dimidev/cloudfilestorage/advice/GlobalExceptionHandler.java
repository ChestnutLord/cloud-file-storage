package com.dimidev.cloudfilestorage.advice;

import com.dimidev.cloudfilestorage.dto.common.ErrorResponse;
import com.dimidev.cloudfilestorage.exception.ApiException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException e) {
        if (e.getStatusCode().is4xxClientError()) {
            log.warn(
                    "Api exception [{}]: {}",
                    e.getClass().getSimpleName(),
                    e.getReason()
            );
        } else {
            log.error(
                    "Api exception [{}]: {}",
                    e.getClass().getSimpleName(),
                    e.getReason(),
                    e
            );
        }

        return ResponseEntity
                .status(e.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(e.getReason()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Ошибка валидации");

        log.warn("Validation failed: {}", message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> violation.getMessage())
                .orElse("Ошибка валидации");

        log.warn("Constraint violation: {}", message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbort(ClientAbortException e) {
        log.debug("Client aborted connection: {}", e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        log.warn("Authentication failed: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse("Неверное имя пользователя или пароль"));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.warn("Upload size exceeded: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse("Размер загружаемого файла превышает допустимый лимит"));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestPart(MissingServletRequestPartException e) {
        log.warn("Missing request part: {}", e.getRequestPartName());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse("Невалидное тело запроса"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestParameter(MissingServletRequestParameterException e) {
        log.warn("Missing request parameter: {}", e.getParameterName());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse("Невалидный или отсутствующий параметр запроса"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException e) {
        log.warn("Unreadable request body: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse("Невалидное тело запроса"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse("Доступ запрещён"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("exception unexpected: message={}", e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse("Неизвестная ошибка"));
    }
}
