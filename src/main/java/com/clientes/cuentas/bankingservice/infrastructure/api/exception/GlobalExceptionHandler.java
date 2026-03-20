package com.clientes.cuentas.bankingservice.infrastructure.api.exception;

import com.clientes.cuentas.bankingservice.domain.exception.AccountTypeNotFoundException;
import com.clientes.cuentas.bankingservice.domain.exception.BankAccountNotFoundException;
import com.clientes.cuentas.bankingservice.domain.exception.CustomerNotFoundException;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidAccountTypeCodeException;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidAmountException;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidCustomerDniException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

/**
 * Global exception handler for the REST API.
 *
 * <p>This class centralizes exception handling across all REST controllers using
 * {@link RestControllerAdvice}. It converts
 * thrown exceptions into standardized HTTP error responses using {@link ProblemDetail},
 * following the RFC 7807 "Problem Details for HTTP APIs" specification.</p>
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
  private static final String NOT_FOUND = "Not Found";
  private static final String UNAUTHORIZED = "Unauthorized";
  private static final String FORBIDDEN = "Forbidden";

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
    // Keep API response generic while logging full server-side context for diagnostics.
    log.error(
            "Unhandled exception. method={} path={} exceptionType={} message={}",
            request.getMethod(),
            request.getRequestURI(),
            ex.getClass().getName(),
            ex.getMessage(),
            ex
    );

    return ProblemDetailHelper.fromHttpRequest(
            HttpStatus.INTERNAL_SERVER_ERROR,
            INTERNAL_SERVER_ERROR,
            "Unexpected error occurred",
            request);
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
    return ProblemDetailHelper.fromHttpRequest(
            HttpStatus.BAD_REQUEST,
            ProblemDetailHelper.BAD_REQUEST,
            ProblemDetailHelper.getConstraintViolationMessage(ex),
            request);
  }

  /**
   * Handles validation errors on request body fields annotated with {@code @Valid}.
   *
   * <p>This method captures {@link MethodArgumentNotValidException} thrown when
   * Bean Validation constraints on a {@code @RequestBody} are violated (e.g., a
   * required field is missing or a value does not match its pattern).
   * The response includes all violated field errors and returns HTTP status
   * {@code 400 Bad Request}.</p>
   *
   * @param ex      the thrown {@link MethodArgumentNotValidException}
   * @param request the current HTTP request
   * @return a {@link ProblemDetail} with the list of field validation errors
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
          @NonNull MethodArgumentNotValidException ex,
          @NonNull HttpHeaders headers,
          @NonNull HttpStatusCode status,
          @NonNull WebRequest request) {
    ProblemDetail problem = ProblemDetailHelper.badRequestFromWebRequest(
            status,
            ProblemDetailHelper.getFieldErrorMessages(ex),
            request);
    return handleExceptionInternal(ex, problem, headers, status, request);
  }

  /**
   * Handles errors caused by malformed or unreadable JSON in the request body.
   *
   * <p>This includes invalid enum values, wrong field types or malformed JSON syntax.
   * When the cause is an {@link InvalidFormatException} targeting an enum field,
   * the response lists the accepted values. Returns HTTP status {@code 400 Bad Request}.</p>
   *
   * @param ex      the thrown {@link HttpMessageNotReadableException}
   * @param request the current HTTP request
   * @return a {@link ProblemDetail} describing the deserialization error
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
          @NonNull HttpMessageNotReadableException ex,
          @NonNull HttpHeaders headers,
          @NonNull HttpStatusCode status,
          @NonNull WebRequest request) {
    ProblemDetail problem = ProblemDetailHelper.badRequestFromWebRequest(
            status,
            ProblemDetailHelper.getHttpMessageNotReadableDetail(ex),
            request);
    return handleExceptionInternal(ex, problem, headers, status, request);
  }

  /**
   * Handles business validation errors when an amount is invalid.
   *
   * @param ex      the thrown exception containing the validation error message
   * @param request the current HTTP request
   * @return a {@link ProblemDetail} representing a 400 Bad Request response
   */
  @ExceptionHandler(InvalidAmountException.class)
  public ProblemDetail handleBadRequests(Exception ex, HttpServletRequest request) {
    return ProblemDetailHelper.fromHttpRequest(
            HttpStatus.BAD_REQUEST,
            ProblemDetailHelper.BAD_REQUEST,
            ex.getMessage(),
            request);
  }

  /**
   * Handles business validation errors for customer DNI and account type code.
   *
   * @param ex      the thrown exception containing the validation error message
   * @param request the current HTTP request
   * @return a {@link ProblemDetail} representing a 400 Bad Request response
   */
  @ExceptionHandler({InvalidCustomerDniException.class, InvalidAccountTypeCodeException.class})
  public ProblemDetail handleInputValidationBadRequests(Exception ex, HttpServletRequest request) {
    return ProblemDetailHelper.fromHttpRequest(
            HttpStatus.BAD_REQUEST,
            ProblemDetailHelper.BAD_REQUEST,
            ex.getMessage(),
            request);
  }

  /**
   * Handles not found errors for account type resources.
   *
   * @param ex      the thrown exception
   * @param request the current HTTP request
   * @return a {@link ProblemDetail} representing a 404 Not Found response
   */
  @ExceptionHandler({AccountTypeNotFoundException.class, BankAccountNotFoundException.class,
          CustomerNotFoundException.class})
  public ProblemDetail handleNotFoundExceptions(Exception ex, HttpServletRequest request) {
    return ProblemDetailHelper.fromHttpRequest(
            HttpStatus.NOT_FOUND,
            NOT_FOUND,
            ex.getMessage(),
            request);
  }

  /**
   * Handles authentication failures when credentials are missing or invalid.
   *
   * @param ex      the thrown authentication exception
   * @param request the current HTTP request
   * @return a {@link ProblemDetail} representing a 401 Unauthorized response
   */
  @ExceptionHandler(AuthenticationException.class)
  public ProblemDetail handleAuthenticationException(AuthenticationException ex,
                                                     HttpServletRequest request) {
    return ProblemDetailHelper.fromHttpRequest(
            HttpStatus.UNAUTHORIZED,
            UNAUTHORIZED,
            Objects.nonNull(ex.getMessage()) && !ex.getMessage().isBlank()
                    ? ex.getMessage()
                    : "Authentication credentials were not provided.",
            request);
  }

  /**
   * Handles authorization failures when user lacks required permissions.
   *
   * @param ex      the thrown access denied exception
   * @param request the current HTTP request
   * @return a {@link ProblemDetail} representing a 403 Forbidden response
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ProblemDetail handleAccessDeniedException(AccessDeniedException ex,
                                                   HttpServletRequest request) {
    return ProblemDetailHelper.fromHttpRequest(
            HttpStatus.FORBIDDEN,
            FORBIDDEN,
            Objects.nonNull(ex.getMessage()) && !ex.getMessage().isBlank()
                    ? ex.getMessage()
                    : "You do not have permission to access this resource.",
            request);
  }
}
