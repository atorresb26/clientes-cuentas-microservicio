package com.clientes.cuentas.bankingservice.infrastructure.api.exception;

import com.clientes.cuentas.bankingservice.domain.exception.AccountTypeNotFoundException;
import com.clientes.cuentas.bankingservice.domain.exception.BankAccountNotFoundException;
import com.clientes.cuentas.bankingservice.domain.exception.CustomerNotFoundException;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidAccountTypeCodeException;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidAmountException;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidCustomerDniException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  private MockHttpServletRequest mockRequest(String uri) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI(uri);
    return request;
  }

  private ServletWebRequest servletWebRequest(String uri) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI(uri);
    return new ServletWebRequest(request, new MockHttpServletResponse());
  }

  // =========================================================================
  // handleGeneric() — 500 Internal Server Error
  // =========================================================================

  @Test
  void shouldReturn500ProblemDetailForUnhandledException() {
    Exception ex = new RuntimeException("Something exploded");

    ProblemDetail result = handler.handleGeneric(ex, mockRequest("/api/resource"));

    assertEquals(500, result.getStatus());
    assertEquals("Internal Server Error", result.getTitle());
    assertEquals("Unexpected error occurred", result.getDetail());
    assertEquals(URI.create("/api/resource"), result.getInstance());
    assertNotNull(result.getProperties());
    assertInstanceOf(Instant.class, result.getProperties().get("timestamp"));
  }

  // =========================================================================
  // handleBadRequests(ConstraintViolationException) — 400
  // =========================================================================

  @Test
  void shouldReturn400ProblemDetailForConstraintViolationWithParameterName() {
    Path.Node node = mock(Path.Node.class);
    when(node.getKind()).thenReturn(ElementKind.PARAMETER);
    when(node.getName()).thenReturn("amount");

    Path path = mock(Path.class);
    when(path.iterator()).thenReturn(List.of(node).iterator());

    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
    when(violation.getPropertyPath()).thenReturn(path);
    when(violation.getMessage()).thenReturn("must be positive");

    ProblemDetail result = handler.handleBadRequests(
            new ConstraintViolationException(Set.of(violation)),
            mockRequest("/api/accounts"));

    assertEquals(400, result.getStatus());
    assertEquals("Bad Request", result.getTitle());
    assertEquals("amount must be positive.", result.getDetail());
    assertNotNull(result.getProperties());
    assertInstanceOf(Instant.class, result.getProperties().get("timestamp"));
  }

  // =========================================================================
  // handleMethodArgumentNotValid() — 400
  // =========================================================================

  @Test
  void shouldReturn400ResponseEntityWithFieldErrorDetailsForMethodArgumentNotValid() {
    BindingResult bindingResult = mock(BindingResult.class);
    when(bindingResult.getFieldErrors()).thenReturn(
            List.of(new FieldError("obj", "dni", "must not be blank")));

    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    when(ex.getBindingResult()).thenReturn(bindingResult);

    ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
            ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, servletWebRequest("/api/customers"));

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    ProblemDetail body = (ProblemDetail) response.getBody();
    assertNotNull(body);
    assertEquals(400, body.getStatus());
    assertEquals("Bad Request", body.getTitle());
    assertEquals("'dni' must not be blank.", body.getDetail());
    assertInstanceOf(Instant.class, body.getProperties().get("timestamp"));
  }

  @Test
  void shouldIncludeAllFieldErrorsInMethodArgumentNotValidResponse() {
    BindingResult bindingResult = mock(BindingResult.class);
    when(bindingResult.getFieldErrors()).thenReturn(List.of(
            new FieldError("obj", "name", "must not be null"),
            new FieldError("obj", "total", "must be positive")));

    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    when(ex.getBindingResult()).thenReturn(bindingResult);

    ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
            ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, servletWebRequest("/api/accounts"));

    assertNotNull(response);
    ProblemDetail body = (ProblemDetail) response.getBody();
    assertNotNull(body);
    assertEquals("'name' must not be null. 'total' must be positive.", body.getDetail());
  }

  // =========================================================================
  // handleHttpMessageNotReadable() — 400
  // =========================================================================

  @Test
  void shouldReturn400ResponseEntityForMalformedJsonBody() {
    HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed JSON");

    ResponseEntity<Object> response = handler.handleHttpMessageNotReadable(
            ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, servletWebRequest("/api/accounts"));

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    ProblemDetail body = (ProblemDetail) response.getBody();
    assertNotNull(body);
    assertEquals(400, body.getStatus());
    assertEquals("Bad Request", body.getTitle());
    assertEquals("Malformed JSON request.", body.getDetail());
    assertNotNull(body.getProperties());
    assertInstanceOf(Instant.class, body.getProperties().get("timestamp"));
  }

  // =========================================================================
  // handleBadRequests(InvalidAmountException) — 400
  // =========================================================================

  @Test
  void shouldReturn400ProblemDetailForInvalidAmountException() {
    InvalidAmountException ex = new InvalidAmountException("Amount must be >= 0");

    ProblemDetail result = handler.handleBadRequests(ex, mockRequest("/api/accounts/1"));

    assertEquals(400, result.getStatus());
    assertEquals("Bad Request", result.getTitle());
    assertEquals("Amount must be >= 0", result.getDetail());
    assertEquals(URI.create("/api/accounts/1"), result.getInstance());
  }

  // =========================================================================
  // handleInputValidationBadRequests() — 400
  // =========================================================================

  @Test
  void shouldReturn400ProblemDetailForInvalidCustomerDniException() {
    InvalidCustomerDniException ex = new InvalidCustomerDniException("Invalid DNI format: 1234");

    ProblemDetail result = handler.handleInputValidationBadRequests(ex, mockRequest("/api/accounts"));

    assertEquals(400, result.getStatus());
    assertEquals("Bad Request", result.getTitle());
    assertEquals("Invalid DNI format: 1234", result.getDetail());
  }

  @Test
  void shouldReturn400ProblemDetailForInvalidAccountTypeCodeException() {
    InvalidAccountTypeCodeException ex = new InvalidAccountTypeCodeException("Unknown code XYZ");

    ProblemDetail result = handler.handleInputValidationBadRequests(ex, mockRequest("/api/accounts"));

    assertEquals(400, result.getStatus());
    assertEquals("Bad Request", result.getTitle());
    assertEquals("Unknown code XYZ", result.getDetail());
  }

  // =========================================================================
  // handleNotFoundExceptions() — 404
  // =========================================================================

  @Test
  void shouldReturn404ProblemDetailForAccountTypeNotFoundException() {
    AccountTypeNotFoundException ex = new AccountTypeNotFoundException("UNKNOWN");

    ProblemDetail result = handler.handleNotFoundExceptions(ex, mockRequest("/api/accounts"));

    assertEquals(404, result.getStatus());
    assertEquals("Not Found", result.getTitle());
    assertEquals(ex.getMessage(), result.getDetail());
    assertEquals(URI.create("/api/accounts"), result.getInstance());
  }

  @Test
  void shouldReturn404ProblemDetailForBankAccountNotFoundException() {
    BankAccountNotFoundException ex = new BankAccountNotFoundException("Account api-123 not found");

    ProblemDetail result = handler.handleNotFoundExceptions(ex, mockRequest("/api/accounts/api-123"));

    assertEquals(404, result.getStatus());
    assertEquals("Not Found", result.getTitle());
    assertEquals("Account api-123 not found", result.getDetail());
  }

  @Test
  void shouldReturn404ProblemDetailForCustomerNotFoundException() {
    CustomerNotFoundException ex = new CustomerNotFoundException("Customer 12345678A not found");

    ProblemDetail result = handler.handleNotFoundExceptions(ex, mockRequest("/api/customers"));

    assertEquals(404, result.getStatus());
    assertEquals("Not Found", result.getTitle());
    assertEquals("Customer 12345678A not found", result.getDetail());
  }
}
