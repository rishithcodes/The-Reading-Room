package com.readingroom;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(ResponseStatusException.class)
 public ResponseEntity<Map<String,Object>> status(ResponseStatusException e){String message=e.getReason()==null?"Request failed":e.getReason();return ResponseEntity.status(e.getStatusCode()).body(Map.of("success",false,"message",message,"error",message,"data",Map.of()));}
 @ExceptionHandler(Exception.class)
 public ResponseEntity<Map<String,Object>> unexpected(Exception e){return ResponseEntity.internalServerError().body(Map.of("success",false,"message","The request could not be completed","error","The request could not be completed","data",Map.of()));}
}
