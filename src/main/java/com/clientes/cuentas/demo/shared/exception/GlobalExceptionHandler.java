package com.clientes.cuentas.demo.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleUnexpectedError(Exception ex, HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

    problem.setTitle("Internal server error");
    problem.setDetail("Unexpected error occurred");
    problem.setType(URI.create("https://api.example.com/problems/internal-error"));
    problem.setInstance(URI.create(request.getRequestURI()));

    return problem;
  }

  // TODO -> Manejador de errores de validacion (@Valid), revisar uso
  /*@ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationError(MethodArgumentNotValidException ex, HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

    problem.setTitle("Validation failed");
    problem.setType(URI.create("https://api.example.com/problems/validation-error"));
    problem.setInstance(URI.create(request.getRequestURI()));

    String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));

    problem.setDetail(message);

    return problem;
  }*/
}
