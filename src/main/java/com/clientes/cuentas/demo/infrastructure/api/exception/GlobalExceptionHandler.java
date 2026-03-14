package com.clientes.cuentas.demo.infrastructure.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

/**
 * Global exception handler for the REST API.
 *
 * <p>This class centralizes exception handling across all REST controllers using
 * {@link RestControllerAdvice}. It converts
 * thrown exceptions into standardized HTTP error responses using {@link ProblemDetail},
 * following the RFC 7807 "Problem Details for HTTP APIs" specification.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles unexpected exceptions that are not explicitly mapped by other handlers.
   *
   * <p>This method captures any {@link Exception} thrown during request processing
   * and converts it into a {@link ProblemDetail} response with HTTP status {@code 500 Internal Server Error}.
   *
   * @param ex the thrown exception
   * @param request the current HTTP request
   * @return a {@link ProblemDetail} representing the internal server error
   */
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGeneric(Exception ex,
                                     HttpServletRequest request) {

    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problem.setTitle("Internal Server Error");
    problem.setDetail("Unexpected error occurred");
    problem.setInstance(URI.create(request.getRequestURI()));
    problem.setProperty("timestamp", Instant.now());

    return problem;
  }

  /**
   * Handles invalid request errors caused by {@link IllegalArgumentException}.
   *
   * <p>This method maps invalid arguments or malformed input to a
   * {@code 400 Bad Request} response. The error detail returned to the client
   * corresponds to the exception message.</p>
   *
   * @param ex the thrown {@link IllegalArgumentException}
   * @param request the current HTTP request
   * @return a {@link ProblemDetail} describing the bad request error
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleBadRequest(IllegalArgumentException ex,
                                        HttpServletRequest request) {

    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problem.setTitle("Invalid request");
    problem.setDetail(ex.getMessage());
    problem.setInstance(URI.create(request.getRequestURI()));
    problem.setProperty("timestamp", Instant.now());

    return problem;
  }
}
