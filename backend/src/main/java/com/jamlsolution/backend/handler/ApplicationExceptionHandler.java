package com.jamlsolution.backend.handler;

import com.jamlsolution.backend.common.ApiResponse;
import com.jamlsolution.backend.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.jamlsolution.backend.exception.ErrorCode.*;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApplicationExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex, HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.builder()
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity
                .status(ex.getErrorCode().getStatus())
                .body(new ApiResponse<>(false, body.getMessage(), null, body, Instant.now(), request.getRequestURI()));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Void>> handleDisabled(DisabledException ex, HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.builder()
                .code(ERR_USER_DISABLED.getCode())
                .message(ERR_USER_DISABLED.getDefaultMessage())
                .build();
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(new ApiResponse<>(false, body.getMessage(), null, body, Instant.now(), request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorResponse.ValidationError> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.add(ErrorResponse.ValidationError.builder()
                    .field(fieldName)
                    .code(message)
                    .message(message)
                    .build());
        });
        ErrorResponse body = ErrorResponse.builder().errors(errors).build();
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ApiResponse<>(false, "Validation failed", null, body, Instant.now(), request.getRequestURI()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.debug(ex.getMessage(), ex);
        ErrorResponse body = ErrorResponse.builder()
                .code(BAD_CREDENTIALS.getCode())
                .message(BAD_CREDENTIALS.getDefaultMessage())
                .build();
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(new ApiResponse<>(false, body.getMessage(), null, body, Instant.now(), request.getRequestURI()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        log.debug(ex.getMessage(), ex);
        ErrorResponse body = ErrorResponse.builder().code("NOT_FOUND").message(ex.getMessage()).build();
        return ResponseEntity
                .status(NOT_FOUND)
                .body(new ApiResponse<>(false, ex.getMessage(), null, body, Instant.now(), request.getRequestURI()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUsernameNotFound(UsernameNotFoundException ex, HttpServletRequest request) {
        log.debug(ex.getMessage(), ex);
        ErrorResponse body = ErrorResponse.builder()
                .code(USERNAME_NOT_FOUND.getCode())
                .message(USERNAME_NOT_FOUND.getDefaultMessage())
                .build();
        return ResponseEntity
                .status(NOT_FOUND)
                .body(new ApiResponse<>(false, body.getMessage(), null, body, Instant.now(), request.getRequestURI()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.debug(ex.getMessage(), ex);
        ErrorResponse body = ErrorResponse.builder()
                .message("You are not authorized to perform this operation")
                .build();
        return ResponseEntity
                .status(FORBIDDEN)
                .body(new ApiResponse<>(false, body.getMessage(), null, body, Instant.now(), request.getRequestURI()));
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidDataAccess(InvalidDataAccessApiUsageException ex, HttpServletRequest request) {
        log.debug(ex.getMessage(), ex);
        ErrorResponse body = ErrorResponse.builder()
                .code("INVALID_SORT")
                .message("Invalid sort parameter. Use valid field names such as: createdDate, name, email")
                .build();
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(new ApiResponse<>(false, body.getMessage(), null, body, Instant.now(), request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error(ex.getMessage(), ex);
        ErrorResponse body = ErrorResponse.builder()
                .code(INTERNAL_EXCEPTION.getCode())
                .message(INTERNAL_EXCEPTION.getDefaultMessage())
                .build();
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, body.getMessage(), null, body, Instant.now(), request.getRequestURI()));
    }
}
