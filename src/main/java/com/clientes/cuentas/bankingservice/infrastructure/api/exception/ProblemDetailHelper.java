package com.clientes.cuentas.bankingservice.infrastructure.api.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utility helper for building and formatting `ProblemDetail` responses.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ProblemDetailHelper {

  static final String TIMESTAMP = "timestamp";
  static final String BAD_REQUEST = "Bad Request";

  /**
   * Builds a {@link ProblemDetail} for errors handled with direct access to the servlet request.
   *
   * <p>Used by exception handlers that receive {@link HttpServletRequest} and need to include
   * the request path as {@code instance} plus the standard {@code timestamp} extension.</p>
   *
   * @param status  HTTP status to expose in the response
   * @param title   short problem title
   * @param detail  human-readable problem description
   * @param request current servlet request
   * @return standardized problem payload
   */
  static ProblemDetail fromHttpRequest(HttpStatus status, String title, String detail, HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatus(status);
    problem.setType(null);
    problem.setTitle(title);
    problem.setDetail(detail);
    problem.setInstance(URI.create(request.getRequestURI()));
    problem.setProperty(TIMESTAMP, Instant.now());
    return problem;
  }

  /**
   * Builds a {@code 4xx} {@link ProblemDetail} from a generic {@link WebRequest}.
   *
   * <p>This is used by {@link ResponseEntityExceptionHandler}
   * overrides where only {@link WebRequest} is available.</p>
   *
   * @param status  HTTP status resolved by Spring
   * @param detail  human-readable problem description
   * @param request current web request
   * @return standardized bad request payload
   */
  static ProblemDetail badRequestFromWebRequest(HttpStatusCode status, String detail, WebRequest request) {
    ProblemDetail problem = ProblemDetail.forStatus(status);
    problem.setType(null);
    problem.setTitle(BAD_REQUEST);
    problem.setDetail(detail);

    URI instance = getRequestUri(request);
    if (instance != null) {
      problem.setInstance(instance);
    }

    problem.setProperty(TIMESTAMP, Instant.now());
    return problem;
  }

  /**
   * Flattens field validation errors from {@link MethodArgumentNotValidException} into one message.
   *
   * @param ex validation exception raised for an invalid {@code @RequestBody}
   * @return concatenated field-level validation messages
   */
  static String getFieldErrorMessages(MethodArgumentNotValidException ex) {
    return ex.getBindingResult().getFieldErrors().stream()
            .map(error -> String.format("'%s' %s.", error.getField(), error.getDefaultMessage()))
            .collect(Collectors.joining(" "));
  }

  /**
   * Extracts a readable message from a {@link ConstraintViolationException}.
   *
   * <p>The message keeps the violated parameter name when available to preserve API clarity.</p>
   *
   * @param ex constraint violation exception
   * @return first formatted violation message or a generic fallback
   */
  static String getConstraintViolationMessage(ConstraintViolationException ex) {
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

  /**
   * Resolves a user-friendly detail for JSON deserialization errors.
   *
   * <p>Handles invalid enums, invalid field formats and malformed JSON bodies while keeping
   * a stable RFC 7807-compatible message contract.</p>
   *
   * @param ex unreadable message exception thrown by Spring MVC
   * @return formatted message suitable for the {@code detail} field
   */
  static String getHttpMessageNotReadableDetail(HttpMessageNotReadableException ex) {
    InvalidFormatException invalidFormatEx = findCause(ex, InvalidFormatException.class);
    if (Objects.nonNull(invalidFormatEx)) {
      return buildInvalidValueMessage(invalidFormatEx.getValue(), getFieldName(invalidFormatEx), invalidFormatEx.getTargetType());
    }

    JsonMappingException mappingException = findCause(ex, JsonMappingException.class);
    IllegalArgumentException illegalArgumentException = findCause(ex, IllegalArgumentException.class);
    Class<?> targetType = resolveTargetType(mappingException);
    if (Objects.nonNull(illegalArgumentException) && Objects.nonNull(targetType) && targetType.isEnum()) {
      return buildInvalidValueMessage(extractUnexpectedValue(illegalArgumentException.getMessage()), getFieldName(mappingException), targetType);
    }

    if (Objects.nonNull(mappingException)) {
      return String.format("Invalid value for field '%s'.", getFieldName(mappingException));
    }
    return "Malformed JSON request.";
  }

  @Nullable
  private static URI getRequestUri(WebRequest request) {
    if (request instanceof ServletWebRequest servletWebRequest) {
      return URI.create(servletWebRequest.getRequest().getRequestURI());
    }
    return null;
  }

  private static String buildInvalidValueMessage(@Nullable Object value, String fieldName, @Nullable Class<?> targetType) {
    if (Objects.nonNull(targetType) && targetType.isEnum()) {
      String validValues = Arrays.stream(targetType.getEnumConstants())
              .map(Object::toString)
              .collect(Collectors.joining(", "));
      return String.format("Invalid value '%s' for field '%s'. Accepted values are: [%s].",
              value, fieldName, validValues);
    }
    return String.format("Invalid value '%s' for field '%s'.", value, fieldName);
  }

  private static String getFieldName(JsonMappingException exception) {
    String fieldName = exception.getPath().stream()
            .map(ref -> Objects.nonNull(ref.getFieldName()) ? ref.getFieldName() : "[" + ref.getIndex() + "]")
            .collect(Collectors.joining("."));
    return fieldName.isBlank() ? "request body" : fieldName;
  }

  @Nullable
  private static Class<?> resolveTargetType(@Nullable JsonMappingException exception) {
    if (exception instanceof InvalidFormatException invalidFormatException) {
      return invalidFormatException.getTargetType();
    }
    if (exception instanceof ValueInstantiationException valueInstantiationException
            && Objects.nonNull(valueInstantiationException.getType())) {
      return valueInstantiationException.getType().getRawClass();
    }
    return null;
  }

  private static String extractUnexpectedValue(@Nullable String message) {
    if (Objects.isNull(message) || !message.contains("Unexpected value '")) {
      return "null";
    }
    int start = message.indexOf('\'') + 1;
    int end = message.indexOf('\'', start);
    return end > start ? message.substring(start, end) : message;
  }

  @Nullable
  private static <T extends Throwable> T findCause(Throwable throwable, Class<T> causeType) {
    Throwable current = throwable;
    while (Objects.nonNull(current)) {
      if (causeType.isInstance(current)) {
        return causeType.cast(current);
      }
      current = current.getCause();
    }
    return null;
  }
}
