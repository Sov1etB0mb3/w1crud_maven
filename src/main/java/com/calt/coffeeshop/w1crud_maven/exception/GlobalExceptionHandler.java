package com.calt.coffeeshop.w1crud_maven.exception;

import com.calt.coffeeshop.w1crud_maven.dto.response.ApiResponse;
import com.calt.coffeeshop.w1crud_maven.enums.ErrorCode;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleConflict(DataIntegrityViolationException e){
        ApiResponse apiResponse =  ApiResponse.builder().build();
        apiResponse.setCode(7);
        apiResponse.setMessage(e.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);

    }
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ApiResponse> handleRuntime(RuntimeException e){
//        ApiResponse apiResponse =  ApiResponse.builder().build();
//        apiResponse.setCode(6);
//        apiResponse.setMessage(e.getMessage());
//        return ResponseEntity.badRequest().body(apiResponse);
//    }
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse> handleAppException(AppException e){
        ApiResponse apiResponse =  ApiResponse.builder().build();
        ErrorCode errorCode= e.getErrorCode();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getCode()).body(apiResponse);
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException e){
        ApiResponse apiResponse = ApiResponse.builder().build();
        apiResponse.setCode(3);
        apiResponse.setMessage(e.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccess(AccessDeniedException e){
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        ApiResponse apiResponse = ApiResponse.builder()
                .message(e.getMessage())
                .code(errorCode.getCode())
                .build();
        return ResponseEntity.status(errorCode.getCode()).body(apiResponse);
    }
}
