package com.lorecraft.tcglounge.common.exception;

import com.lorecraft.tcglounge.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
        log.error("Business Exception: {}", e.getMessage(), e);
        ErrorCode errorCode = e.getErrorCode();
        
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponse<Object>> handleValidationException(Exception e) {
        log.error("Validation Exception: {}", e.getMessage(), e);
        
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.error("Invalid input parameters"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception e) {
        log.error("Unexpected Exception: {}", e.getMessage(), e);
        
        return ResponseEntity
            .internalServerError()
            .body(ApiResponse.error("Internal server error"));
    }
}