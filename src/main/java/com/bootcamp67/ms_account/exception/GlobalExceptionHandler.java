package com.bootcamp67.ms_account.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public Mono<ResponseEntity<Map<String,String>>> handleNotFound(NotFoundException ex){
    return Mono.just(ResponseEntity.status(404)
        .body(Map.of("error",ex.getMessage())));
  }

  @ExceptionHandler(BusinessException.class)
  public Mono<ResponseEntity<Map<String,String>>> handleBusiness(BusinessException ex){
    return Mono.just(ResponseEntity.badRequest()
        .body(Map.of("error",ex.getMessage())));
  }

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<Map<String,String>>> handleAll(Exception ex){
    return Mono.just(ResponseEntity.status(500)
        .body(Map.of("error",ex.getMessage())));
  }
}
