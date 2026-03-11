package com.calt.coffeeshop.w1crud_maven.exception;

import com.calt.coffeeshop.w1crud_maven.dto.responsedto.ApiResponseDto;
import com.calt.coffeeshop.w1crud_maven.enums.ErrorCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDto> handleConflict(DataIntegrityViolationException e){
        ApiResponseDto apiResponseDto =  ApiResponseDto.builder().build();
        apiResponseDto.setCode(7);
        apiResponseDto.setMessage(e.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponseDto);

    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseDto> handleRuntime(RuntimeException e){
        ApiResponseDto apiResponseDto =  ApiResponseDto.builder().build();
        apiResponseDto.setCode(6);
        apiResponseDto.setMessage(e.getMessage());
        return ResponseEntity.badRequest().body(apiResponseDto);
    }
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponseDto> handleAppException(AppException e){
        ApiResponseDto apiResponseDto =  ApiResponseDto.builder().build();
        ErrorCode errorCode= e.getErrorCode();
        apiResponseDto.setCode(errorCode.getCode());
        apiResponseDto.setMessage(errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponseDto);
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto> handleValidation(MethodArgumentNotValidException e){
        ApiResponseDto apiResponseDto = ApiResponseDto.builder().build();
        apiResponseDto.setCode(5);
        apiResponseDto.setMessage(e.getMessage());
        return ResponseEntity.badRequest().body(apiResponseDto);
    }
}
