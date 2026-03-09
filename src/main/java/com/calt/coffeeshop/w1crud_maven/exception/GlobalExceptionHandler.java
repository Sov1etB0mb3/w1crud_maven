package com.calt.coffeeshop.w1crud_maven.exception;

import com.calt.coffeeshop.w1crud_maven.dto.responsedto.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleConflict(DataIntegrityViolationException e){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(7);
        apiResponse.setMessage(e.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);

    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntime(RuntimeException e){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(6);
        apiResponse.setMessage(e.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse> handleAppException(AppException e){
        ApiResponse apiResponse = new ApiResponse();
        ErrorCode errorCode= e.getErrorCode();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException e){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(5);
        apiResponse.setMessage(e.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }
}
