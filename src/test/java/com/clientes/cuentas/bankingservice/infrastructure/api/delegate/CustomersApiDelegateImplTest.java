package com.clientes.cuentas.bankingservice.infrastructure.api.delegate;

import com.clientes.cuentas.bankingservice.application.pagination.PageResult;
import com.clientes.cuentas.bankingservice.application.pagination.PaginationCriteria;
import com.clientes.cuentas.bankingservice.application.usecase.GetAdultCustomersUseCase;
import com.clientes.cuentas.bankingservice.application.usecase.GetCustomerByDniUseCase;
import com.clientes.cuentas.bankingservice.application.usecase.GetCustomersUseCase;
import com.clientes.cuentas.bankingservice.application.usecase.GetCustomersWithHigherAmountUseCase;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.model.vo.Dni;
import com.clientes.cuentas.bankingservice.infrastructure.api.mapper.CustomerApiMapper;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.CustomerAccountDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.PaginatedCustomerAccountDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.PaginatedCustomerDTO;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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


  @Test
  void shouldReturn200OkWithPaginatedCustomerAccountDtoList() {
    List<Customer> customers = List.of(new Customer(), new Customer());
    PageResult<Customer> pageResult = PageResult.<Customer>builder()
            .content(customers)
            .pageNumber(1)
            .pageSize(2)
            .totalElements(5)
            .totalPages(3)
            .isFirst(false)
            .isLast(false)
            .build();
    PaginatedCustomerAccountDTO expectedResponse = new PaginatedCustomerAccountDTO();
    expectedResponse.setContent(List.of(new CustomerAccountDTO(), new CustomerAccountDTO()));
    expectedResponse.setCurrentPage(1);
    expectedResponse.setPageSize(2);
    expectedResponse.setTotalElements(5L);
    expectedResponse.setTotalPages(3);
    expectedResponse.setIsFirst(false);
    expectedResponse.setIsLast(false);
    expectedResponse.setHasNext(true);
    expectedResponse.setHasPrevious(true);

    when(getCustomersUseCase.execute(any(PaginationCriteria.class))).thenReturn(pageResult);
    when(mapper.toPaginatedCustomerAccountDto(pageResult)).thenReturn(expectedResponse);

    ResponseEntity<PaginatedCustomerAccountDTO> response = delegate.getCustomersAndAccounts(0, 20, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertSame(expectedResponse.getContent(), response.getBody().getContent());
    assertEquals(1, response.getBody().getCurrentPage());
    assertEquals(2, response.getBody().getPageSize());
    assertEquals(5L, response.getBody().getTotalElements());
    assertEquals(3, response.getBody().getTotalPages());
    assertEquals(false, response.getBody().getIsFirst());
    assertEquals(false, response.getBody().getIsLast());
    assertEquals(true, response.getBody().getHasNext());
    assertEquals(true, response.getBody().getHasPrevious());

    verify(getCustomersUseCase).execute(any(PaginationCriteria.class));
    verify(mapper).toPaginatedCustomerAccountDto(pageResult);
  }

  @Test
  void shouldReturn200OkWithEmptyPaginatedListWhenNoCustomersAndAccountsExist() {
    PageResult<Customer> emptyPage = PageResult.<Customer>builder()
            .content(List.of())
            .pageNumber(0)
            .pageSize(20)
            .totalElements(0)
            .totalPages(0)
            .isFirst(true)
            .isLast(true)
            .build();

    PaginatedCustomerAccountDTO expectedResponse = new PaginatedCustomerAccountDTO();
    expectedResponse.setContent(List.of());
    when(getCustomersUseCase.execute(any(PaginationCriteria.class))).thenReturn(emptyPage);
    when(mapper.toPaginatedCustomerAccountDto(emptyPage)).thenReturn(expectedResponse);

    ResponseEntity<PaginatedCustomerAccountDTO> response = delegate.getCustomersAndAccounts(0, 20, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getContent().isEmpty());
  }

  @Test
  void shouldReturn200OkWithPaginatedAdultCustomerDtoList() {
    List<Customer> adults = List.of(new Customer());
    PageResult<Customer> pageResult = PageResult.<Customer>builder()
            .content(adults)
            .pageNumber(0)
            .pageSize(20)
            .totalElements(21)
            .totalPages(2)
            .isFirst(true)
            .isLast(false)
            .build();
    PaginatedCustomerDTO expectedResponse = new PaginatedCustomerDTO();
    expectedResponse.setContent(List.of());
    expectedResponse.setCurrentPage(0);
    expectedResponse.setPageSize(20);
    expectedResponse.setTotalElements(21L);
    expectedResponse.setTotalPages(2);
    expectedResponse.setIsFirst(true);
    expectedResponse.setIsLast(false);
    expectedResponse.setHasNext(true);
    expectedResponse.setHasPrevious(false);

    when(getAdultCustomersUseCase.execute(any(PaginationCriteria.class))).thenReturn(pageResult);
    when(mapper.toPaginatedCustomerDto(pageResult)).thenReturn(expectedResponse);

    ResponseEntity<PaginatedCustomerDTO> response = delegate.getAdultCustomers(0, 20, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertSame(expectedResponse.getContent(), response.getBody().getContent());
    assertEquals(0, response.getBody().getCurrentPage());
    assertEquals(20, response.getBody().getPageSize());
    assertEquals(21L, response.getBody().getTotalElements());
    assertEquals(2, response.getBody().getTotalPages());
    assertEquals(true, response.getBody().getIsFirst());
    assertEquals(false, response.getBody().getIsLast());
    assertEquals(true, response.getBody().getHasNext());
    assertEquals(false, response.getBody().getHasPrevious());

    verify(getAdultCustomersUseCase).execute(any(PaginationCriteria.class));
    verify(mapper).toPaginatedCustomerDto(pageResult);
  }

  @Test
  void shouldReturn200OkWithEmptyPaginatedListWhenNoAdultCustomersFound() {
    PageResult<Customer> emptyPage = PageResult.<Customer>builder()
            .content(List.of())
            .pageNumber(0)
            .pageSize(20)
            .totalElements(0)
            .totalPages(0)
            .isFirst(true)
            .isLast(true)
            .build();

    PaginatedCustomerDTO expectedResponse = new PaginatedCustomerDTO();
    expectedResponse.setContent(List.of());
    when(getAdultCustomersUseCase.execute(any(PaginationCriteria.class))).thenReturn(emptyPage);
    when(mapper.toPaginatedCustomerDto(emptyPage)).thenReturn(expectedResponse);

    ResponseEntity<PaginatedCustomerDTO> response = delegate.getAdultCustomers(0, 20, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getContent().isEmpty());
  }

  @Test
  void shouldReturn200OkWithPaginatedDtoListForHigherAmount() {
    BigDecimal cantidad = new BigDecimal("500.00");
    List<Customer> customers = List.of(new Customer());
    PageResult<Customer> pageResult = PageResult.<Customer>builder()
            .content(customers)
            .pageNumber(0)
            .pageSize(20)
            .totalElements(1)
            .totalPages(1)
            .isFirst(true)
            .isLast(true)
            .build();
    PaginatedCustomerDTO expectedResponse = new PaginatedCustomerDTO();
    expectedResponse.setContent(List.of());

    when(getCustomersWithHigherAmountUseCase.execute(eq(cantidad), any(PaginationCriteria.class)))
            .thenReturn(pageResult);
    when(mapper.toPaginatedCustomerDto(pageResult)).thenReturn(expectedResponse);

    ResponseEntity<PaginatedCustomerDTO> response = delegate.getCustomersWithHigherAmount(cantidad, 0, 20, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertSame(expectedResponse.getContent(), response.getBody().getContent());

    verify(getCustomersWithHigherAmountUseCase).execute(eq(cantidad), any(PaginationCriteria.class));
    verify(mapper).toPaginatedCustomerDto(pageResult);
  }

  @Test
  void shouldPassCantidadParameterDirectlyToUseCase() {
    BigDecimal cantidad = new BigDecimal("1000.00");
    PageResult<Customer> emptyPage = PageResult.<Customer>builder()
            .content(List.of())
            .pageNumber(0)
            .pageSize(20)
            .totalElements(0)
            .totalPages(0)
            .isFirst(true)
            .isLast(true)
            .build();

    PaginatedCustomerDTO expectedResponse = new PaginatedCustomerDTO();
    expectedResponse.setContent(List.of());
    when(getCustomersWithHigherAmountUseCase.execute(eq(cantidad), any(PaginationCriteria.class)))
            .thenReturn(emptyPage);
    when(mapper.toPaginatedCustomerDto(emptyPage)).thenReturn(expectedResponse);

    delegate.getCustomersWithHigherAmount(cantidad, 0, 20, null);

    verify(getCustomersWithHigherAmountUseCase).execute(eq(cantidad), any(PaginationCriteria.class));
  }

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
