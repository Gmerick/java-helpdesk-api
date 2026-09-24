package io.github.gmerick.helpdesk;

import java.util.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiErrors {
  private ResponseEntity<ProblemDetail> problem(HttpStatus status, String message) {
    return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, message));
  }

  @ExceptionHandler(BusinessException.class)
  ResponseEntity<ProblemDetail> business(BusinessException e) {
    return problem(e.status(), e.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ProblemDetail> validation(MethodArgumentNotValidException e) {
    ProblemDetail detail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Verifique os campos enviados.");
    Map<String, String> errors = new TreeMap<>();
    e.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
    detail.setProperty("errors", errors);
    return ResponseEntity.badRequest().body(detail);
  }

  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MethodArgumentTypeMismatchException.class
  })
  ResponseEntity<ProblemDetail> malformed(Exception e) {
    return problem(HttpStatus.BAD_REQUEST, "JSON, identificador ou enum inválido.");
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<ProblemDetail> conflict(DataIntegrityViolationException e) {
    return problem(HttpStatus.CONFLICT, "Registro duplicado ou conflito de integridade.");
  }
}
