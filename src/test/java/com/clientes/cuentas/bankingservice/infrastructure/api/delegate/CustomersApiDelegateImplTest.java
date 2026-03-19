package com.clientes.cuentas.bankingservice.infrastructure.api.delegate;

import com.clientes.cuentas.bankingservice.application.usecase.GetAdultCustomersUseCase;
import com.clientes.cuentas.bankingservice.application.usecase.GetCustomerByDniUseCase;
import com.clientes.cuentas.bankingservice.application.usecase.GetCustomersUseCase;
import com.clientes.cuentas.bankingservice.application.usecase.GetCustomersWithHigherAmountUseCase;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.model.vo.Dni;
import com.clientes.cuentas.bankingservice.infrastructure.api.mapper.CustomerApiMapper;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.CustomerAccountDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.CustomerDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomersApiDelegateImplTest {

  @InjectMocks
  private CustomersApiDelegateImpl delegate;

  @Mock
  private GetCustomersUseCase getCustomersUseCase;

  @Mock
  private GetAdultCustomersUseCase getAdultCustomersUseCase;

  @Mock
  private GetCustomersWithHigherAmountUseCase getCustomersWithHigherAmountUseCase;

  @Mock
  private GetCustomerByDniUseCase getCustomerByDniUseCase;

  @Mock
  private CustomerApiMapper mapper;

  // =========================================================================
  // getCustomersAndAccounts()
  // =========================================================================

  @Test
  void shouldReturn200OkWithMappedCustomerAccountDtoList() {
    List<Customer> customers = List.of(new Customer(), new Customer());
    List<CustomerAccountDTO> expectedDtos = List.of(new CustomerAccountDTO(), new CustomerAccountDTO());

    when(getCustomersUseCase.execute()).thenReturn(customers);
    when(mapper.toCustomerAccountDtoList(customers)).thenReturn(expectedDtos);

    ResponseEntity<List<CustomerAccountDTO>> response = delegate.getCustomersAndAccounts();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(expectedDtos, response.getBody());

    verify(getCustomersUseCase).execute();
    verify(mapper).toCustomerAccountDtoList(customers);
    verifyNoMoreInteractions(getCustomersUseCase, getAdultCustomersUseCase,
            getCustomersWithHigherAmountUseCase, getCustomerByDniUseCase, mapper);
  }

  @Test
  void shouldReturn200OkWithEmptyListWhenNoCustomersAndAccountsExist() {
    when(getCustomersUseCase.execute()).thenReturn(List.of());
    when(mapper.toCustomerAccountDtoList(List.of())).thenReturn(List.of());

    ResponseEntity<List<CustomerAccountDTO>> response = delegate.getCustomersAndAccounts();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isEmpty());
  }

  // =========================================================================
  // getAdultCustomers()
  // =========================================================================

  @Test
  void shouldReturn200OkWithMappedAdultCustomerDtoList() {
    List<Customer> adults = List.of(new Customer());
    List<CustomerDTO> expectedDtos = List.of(new CustomerDTO());

    when(getAdultCustomersUseCase.execute()).thenReturn(adults);
    when(mapper.toCustomerDtoList(adults)).thenReturn(expectedDtos);

    ResponseEntity<List<CustomerDTO>> response = delegate.getAdultCustomers();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(expectedDtos, response.getBody());

    verify(getAdultCustomersUseCase).execute();
    verify(mapper).toCustomerDtoList(adults);
    verifyNoMoreInteractions(getCustomersUseCase, getAdultCustomersUseCase,
            getCustomersWithHigherAmountUseCase, getCustomerByDniUseCase, mapper);
  }

  @Test
  void shouldReturn200OkWithEmptyListWhenNoAdultCustomersFound() {
    when(getAdultCustomersUseCase.execute()).thenReturn(List.of());
    when(mapper.toCustomerDtoList(List.of())).thenReturn(List.of());

    ResponseEntity<List<CustomerDTO>> response = delegate.getAdultCustomers();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isEmpty());
  }

  // =========================================================================
  // getCustomersWithHigherAmount()
  // =========================================================================

  @Test
  void shouldReturn200OkWithMappedDtoListForHigherAmount() {
    BigDecimal cantidad = new BigDecimal("500.00");
    List<Customer> customers = List.of(new Customer());
    List<CustomerDTO> expectedDtos = List.of(new CustomerDTO());

    when(getCustomersWithHigherAmountUseCase.execute(cantidad)).thenReturn(customers);
    when(mapper.toCustomerDtoList(customers)).thenReturn(expectedDtos);

    ResponseEntity<List<CustomerDTO>> response = delegate.getCustomersWithHigherAmount(cantidad);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(expectedDtos, response.getBody());

    verify(getCustomersWithHigherAmountUseCase).execute(cantidad);
    verify(mapper).toCustomerDtoList(customers);
    verifyNoMoreInteractions(getCustomersUseCase, getAdultCustomersUseCase,
            getCustomersWithHigherAmountUseCase, getCustomerByDniUseCase, mapper);
  }

  @Test
  void shouldPassCantidadParameterDirectlyToUseCase() {
    BigDecimal cantidad = new BigDecimal("1000.00");

    when(getCustomersWithHigherAmountUseCase.execute(cantidad)).thenReturn(List.of());
    when(mapper.toCustomerDtoList(List.of())).thenReturn(List.of());

    delegate.getCustomersWithHigherAmount(cantidad);

    verify(getCustomersWithHigherAmountUseCase).execute(new BigDecimal("1000.00"));
  }

  // =========================================================================
  // getCustomerByDni()
  // =========================================================================

  @Test
  void shouldReturn200OkWithMappedCustomerAccountDtoForDni() {
    String dni = "12345678A";
    Customer customer = Customer.builder().dni(Dni.of(dni)).build();
    CustomerAccountDTO expectedDto = new CustomerAccountDTO();

    when(getCustomerByDniUseCase.execute(dni)).thenReturn(customer);
    when(mapper.toCustomerAccountDto(customer)).thenReturn(expectedDto);

    ResponseEntity<CustomerAccountDTO> response = delegate.getCustomerByDni(dni);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(expectedDto, response.getBody());

    verify(getCustomerByDniUseCase).execute(dni);
    verify(mapper).toCustomerAccountDto(customer);
    verifyNoMoreInteractions(getCustomersUseCase, getAdultCustomersUseCase,
            getCustomersWithHigherAmountUseCase, getCustomerByDniUseCase, mapper);
  }

  @Test
  void shouldPassDniParameterDirectlyToUseCase() {
    String dni = "99999999Z";
    Customer customer = Customer.builder().dni(Dni.of(dni)).build();

    when(getCustomerByDniUseCase.execute(dni)).thenReturn(customer);
    when(mapper.toCustomerAccountDto(customer)).thenReturn(new CustomerAccountDTO());

    delegate.getCustomerByDni(dni);

    verify(getCustomerByDniUseCase).execute("99999999Z");
  }
}

