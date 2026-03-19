package com.clientes.cuentas.bankingservice.infrastructure.api.exception;

import com.clientes.cuentas.bankingservice.domain.enums.AccountType;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProblemDetailHelperTest {

  @Test
  void shouldBuildProblemDetailWithAllFieldsFromHttpRequest() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/resource");

    ProblemDetail result = ProblemDetailHelper.fromHttpRequest(
            HttpStatus.BAD_REQUEST, "Bad Request", "Detail message", request);

    assertEquals(400, result.getStatus());
    assertEquals("Bad Request", result.getTitle());
    assertEquals("Detail message", result.getDetail());
    assertEquals(URI.create("/api/resource"), result.getInstance());
    Assertions.assertNotNull(result.getProperties());
    assertInstanceOf(Instant.class, result.getProperties().get(ProblemDetailHelper.TIMESTAMP));
  }

  @Test
  void shouldSetCorrectStatusCodeFromHttpRequest() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/items/1");

    ProblemDetail result = ProblemDetailHelper.fromHttpRequest(
            HttpStatus.NOT_FOUND, "Not Found", "Item not found", request);

    assertEquals(404, result.getStatus());
    assertEquals("Not Found", result.getTitle());
    assertEquals("Item not found", result.getDetail());
    assertEquals(URI.create("/api/items/1"), result.getInstance());
  }

  @Test
  void shouldSetInstanceToRequestUriFromHttpRequest() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/api/customers/12345678A");

    ProblemDetail result = ProblemDetailHelper.fromHttpRequest(
            HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Unexpected", request);

    assertEquals(URI.create("/api/customers/12345678A"), result.getInstance());
  }

  @Test
  void shouldBuildProblemDetailWithAllFieldsFromServletWebRequest() {
    MockHttpServletRequest servletRequest = new MockHttpServletRequest();
    servletRequest.setRequestURI("/api/accounts");
    ServletWebRequest webRequest = new ServletWebRequest(servletRequest);

    ProblemDetail result = ProblemDetailHelper.badRequestFromWebRequest(
            HttpStatus.BAD_REQUEST, "Validation failed", webRequest);

    assertEquals(400, result.getStatus());
    assertEquals(ProblemDetailHelper.BAD_REQUEST, result.getTitle());
    assertEquals("Validation failed", result.getDetail());
    assertEquals(URI.create("/api/accounts"), result.getInstance());
    Assertions.assertNotNull(result.getProperties());
    assertInstanceOf(Instant.class, result.getProperties().get(ProblemDetailHelper.TIMESTAMP));
  }

  @Test
  void shouldNotSetInstanceWhenWebRequestIsNotServletRequest() {
    WebRequest genericWebRequest = mock(WebRequest.class);

    ProblemDetail result = ProblemDetailHelper.badRequestFromWebRequest(
            HttpStatus.BAD_REQUEST, "Error", genericWebRequest);

    assertEquals(400, result.getStatus());
    assertEquals(ProblemDetailHelper.BAD_REQUEST, result.getTitle());
    assertNull(result.getInstance());
  }

  @Test
  void shouldFormatSingleFieldErrorMessage() {
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);
    when(ex.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(
            List.of(new FieldError("obj", "name", "must not be blank")));

    assertEquals("'name' must not be blank.", ProblemDetailHelper.getFieldErrorMessages(ex));
  }

  @Test
  void shouldJoinMultipleFieldErrorMessagesWithSpace() {
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);
    when(ex.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(List.of(
            new FieldError("obj", "dni", "must not be null"),
            new FieldError("obj", "total", "must be positive")));

    assertEquals("'dni' must not be null. 'total' must be positive.",
            ProblemDetailHelper.getFieldErrorMessages(ex));
  }

  @Test
  void shouldReturnEmptyStringWhenNoFieldErrorsExist() {
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);
    when(ex.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(List.of());

    assertEquals("", ProblemDetailHelper.getFieldErrorMessages(ex));
  }

  @Test
  void shouldUseParameterNodeNameInViolationMessage() {
    Path.Node node = mock(Path.Node.class);
    when(node.getKind()).thenReturn(ElementKind.PARAMETER);
    when(node.getName()).thenReturn("amount");

    Path path = mock(Path.class);
    when(path.iterator()).thenReturn(List.of(node).iterator());

    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
    when(violation.getPropertyPath()).thenReturn(path);
    when(violation.getMessage()).thenReturn("must be greater than or equal to 0");

    String result = ProblemDetailHelper.getConstraintViolationMessage(
            new ConstraintViolationException(Set.of(violation)));

    assertEquals("amount must be greater than or equal to 0.", result);
  }

  @Test
  void shouldOmitPropertyNameWhenNodeKindIsNotParameter() {
    Path.Node node = mock(Path.Node.class);
    when(node.getKind()).thenReturn(ElementKind.PROPERTY); // not PARAMETER

    Path path = mock(Path.class);
    when(path.iterator()).thenReturn(List.of(node).iterator());

    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
    when(violation.getPropertyPath()).thenReturn(path);
    when(violation.getMessage()).thenReturn("must not be null");

    // propertyName stays "" → " must not be null."
    assertEquals(" must not be null.", ProblemDetailHelper.getConstraintViolationMessage(
            new ConstraintViolationException(Set.of(violation))));
  }

  @Test
  void shouldReturnFallbackMessageWhenViolationSetIsEmpty() {
    assertEquals("Invalid Request", ProblemDetailHelper.getConstraintViolationMessage(
            new ConstraintViolationException(Set.of())));
  }

  @Test
  void shouldListAcceptedEnumValuesWhenInvalidFormatTargetsEnum() {
    InvalidFormatException invalidFormatEx = mock(InvalidFormatException.class);
    when(invalidFormatEx.getValue()).thenReturn("WRONG_CODE");
    when(invalidFormatEx.getTargetType()).thenReturn((Class) AccountType.class);
    when(invalidFormatEx.getPath()).thenReturn(
            List.of(new JsonMappingException.Reference(null, "accountType")));

    HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
            "Invalid format", invalidFormatEx, mock(HttpInputMessage.class));

    assertEquals(
            "Invalid value 'WRONG_CODE' for field 'accountType'. Accepted values are: [JUNIOR, NORMAL, PREMIUM].",
            ProblemDetailHelper.getHttpMessageNotReadableDetail(ex));
  }

  @Test
  void shouldReturnSimpleInvalidValueMessageWhenInvalidFormatTargetsNonEnum() {
    InvalidFormatException invalidFormatEx = mock(InvalidFormatException.class);
    when(invalidFormatEx.getValue()).thenReturn("not-a-date");
    when(invalidFormatEx.getTargetType()).thenReturn((Class) LocalDate.class);
    when(invalidFormatEx.getPath()).thenReturn(
            List.of(new JsonMappingException.Reference(null, "birthDate")));

    HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
            "Invalid format", invalidFormatEx, mock(HttpInputMessage.class));

    assertEquals("Invalid value 'not-a-date' for field 'birthDate'.",
            ProblemDetailHelper.getHttpMessageNotReadableDetail(ex));
  }

  @Test
  void shouldUseRequestBodyAsFieldNameWhenPathIsEmpty() {
    InvalidFormatException invalidFormatEx = mock(InvalidFormatException.class);
    when(invalidFormatEx.getValue()).thenReturn("badValue");
    when(invalidFormatEx.getTargetType()).thenReturn((Class) String.class);
    when(invalidFormatEx.getPath()).thenReturn(List.of()); // empty path

    HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
            "Invalid format", invalidFormatEx, mock(HttpInputMessage.class));

    assertEquals("Invalid value 'badValue' for field 'request body'.",
            ProblemDetailHelper.getHttpMessageNotReadableDetail(ex));
  }

  @Test
  void shouldListAcceptedEnumValuesWhenValueInstantiationWrapsUnexpectedValueError() {
    // Simulates: @JsonCreator throws IllegalArgumentException("Unexpected value 'BADCODE'")
    // wrapped inside a ValueInstantiationException
    IllegalArgumentException illegalArgEx =
            new IllegalArgumentException("Unexpected value 'BADCODE'");

    JavaType javaType = mock(JavaType.class);
    when(javaType.getRawClass()).thenReturn((Class) AccountType.class);

    ValueInstantiationException valueInstantiationEx = mock(ValueInstantiationException.class);
    when(valueInstantiationEx.getType()).thenReturn(javaType);
    when(valueInstantiationEx.getCause()).thenReturn(illegalArgEx);
    when(valueInstantiationEx.getPath()).thenReturn(
            List.of(new JsonMappingException.Reference(null, "codTipoCuenta")));

    HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
            "Instantiation failed", valueInstantiationEx, mock(HttpInputMessage.class));

    assertEquals(
            "Invalid value 'BADCODE' for field 'codTipoCuenta'. Accepted values are: [JUNIOR, NORMAL, PREMIUM].",
            ProblemDetailHelper.getHttpMessageNotReadableDetail(ex));
  }

  @Test
  void shouldReturnInvalidValueForFieldWhenOnlyJsonMappingExceptionIsPresent() {
    JsonMappingException jsonMappingEx = mock(JsonMappingException.class);
    when(jsonMappingEx.getPath()).thenReturn(
            List.of(new JsonMappingException.Reference(null, "someField")));
    // getCause() returns null by default on the mock → no IllegalArgumentException in chain

    HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
            "Mapping error", jsonMappingEx, mock(HttpInputMessage.class));

    assertEquals("Invalid value for field 'someField'.",
            ProblemDetailHelper.getHttpMessageNotReadableDetail(ex));
  }

  @Test
  void shouldBuildNestedFieldNameFromMultiplePathReferences() {
    JsonMappingException jsonMappingEx = mock(JsonMappingException.class);
    when(jsonMappingEx.getPath()).thenReturn(List.of(
            new JsonMappingException.Reference(null, "parent"),
            new JsonMappingException.Reference(null, "child")));

    HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
            "Mapping error", jsonMappingEx, mock(HttpInputMessage.class));

    assertEquals("Invalid value for field 'parent.child'.",
            ProblemDetailHelper.getHttpMessageNotReadableDetail(ex));
  }

  @Test
  void shouldReturnMalformedJsonMessageWhenNoCauseIsRecognised() {
    HttpMessageNotReadableException ex =
            new HttpMessageNotReadableException("Malformed JSON");

    assertEquals("Malformed JSON request.",
            ProblemDetailHelper.getHttpMessageNotReadableDetail(ex));
  }
}
