package com.clientes.cuentas.demo.infrastructure.api.exception;

import com.clientes.cuentas.demo.domain.exception.AccountTypeNotFoundException;
import com.clientes.cuentas.demo.domain.exception.InvalidAmountException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
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
   * @param ex      the thrown exception
   * @param request the current HTTP request
   * @return a {@link ProblemDetail} representing the internal server error
   */
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGeneric(Exception ex, HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problem.setTitle("Internal Server Error");
    problem.setDetail("Unexpected error occurred");
    problem.setInstance(URI.create(request.getRequestURI()));
    problem.setProperty("timestamp", Instant.now());

    return problem;
  }

  /**
   * Handles validation errors caused by {@link ConstraintViolationException}.
   *
   * <p>This method captures constraint violations triggered during request
   * processing (for example, invalid path parameters, query parameters, or
   * request body validation) and converts them into a {@link ProblemDetail}
   * response with HTTP status {@code 400 Bad Request}. The response detail
   * contains a formatted message describing the violated constraints.</p>
   *
   * @param ex      the thrown {@link ConstraintViolationException}
   * @param request the current HTTP request
   * @return a {@link ProblemDetail} representing the validation error
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ProblemDetail handleBadRequests(ConstraintViolationException ex, HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problem.setTitle("Bad Request");
    problem.setDetail(getConstraintViolationMessage(ex));
    problem.setInstance(URI.create(request.getRequestURI()));
    problem.setProperty("timestamp", Instant.now());

    return problem;
  }

  private String getConstraintViolationMessage(ConstraintViolationException ex) {
    return ex.getConstraintViolations().stream()
            .map(v -> {
              String propertyName = "";
              for (var node : v.getPropertyPath()) {
                if (node.getKind().equals(ElementKind.PARAMETER)) {
                  propertyName = node.getName();
                }
              }
              return String.format("%s %s.", propertyName, v.getMessage());
            })
            .findFirst()
            .orElse("Invalid Request");
  }

  @ExceptionHandler(InvalidAmountException.class)
  public ProblemDetail handleBadRequests(Exception ex, HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problem.setTitle("Bad Request");
    problem.setDetail(ex.getMessage());
    problem.setInstance(URI.create(request.getRequestURI()));
    problem.setProperty("timestamp", Instant.now());

    return problem;
  }

  @ExceptionHandler({AccountTypeNotFoundException.class})
  public ProblemDetail handleNotFoundExceptions(Exception ex, HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    problem.setTitle("Not Found");
    problem.setDetail(ex.getMessage());
    problem.setInstance(URI.create(request.getRequestURI()));
    problem.setProperty("timestamp", Instant.now());

    return problem;
  }
}
