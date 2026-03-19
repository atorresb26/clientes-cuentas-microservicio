package com.clientes.cuentas.bankingservice.infrastructure.persistence.repository;

import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.model.vo.Dni;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.CustomerEntity;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper.CustomerAccountAssembler;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper.CustomerEntityMapper;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.projection.CustomerAccountRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRepositoryAdapterTest {

  @InjectMocks
  private CustomerRepositoryAdapter adapter;

  @Mock
  private JpaCustomerRepository jpaCustomerRepository;

  @Mock
  private CustomerAccountAssembler customerAccountAssembler;

  @Mock
  private CustomerEntityMapper mapper;


  @Test
  void shouldReturnPaginatedCustomersAndAccounts() {
    CustomerAccountRow row = mock(CustomerAccountRow.class);
    Customer customer = new Customer();
    Page<CustomerAccountRow> page = new PageImpl<>(List.of(row), PageRequest.of(0, 20), 1);

    when(jpaCustomerRepository.getCustomersAndAccountsPaginated(any())).thenReturn(page);
    when(customerAccountAssembler.toCustomers(List.of(row))).thenReturn(List.of(customer));

    var result = adapter.getCustomersAndAccountsPaginated(PageRequest.of(0, 20));

    assertEquals(1, result.getContent().size());
    assertSame(customer, result.getContent().getFirst());
    verify(jpaCustomerRepository).getCustomersAndAccountsPaginated(any());
    verify(customerAccountAssembler).toCustomers(List.of(row));
    verifyNoMoreInteractions(jpaCustomerRepository, customerAccountAssembler);
    verifyNoInteractions(mapper);
  }

  @Test
  void shouldReturnEmptyPageWhenNoCustomersAndAccountsExist() {
    Page<CustomerAccountRow> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

    when(jpaCustomerRepository.getCustomersAndAccountsPaginated(any())).thenReturn(emptyPage);
    when(customerAccountAssembler.toCustomers(List.of())).thenReturn(List.of());

    var result = adapter.getCustomersAndAccountsPaginated(PageRequest.of(0, 20));

    assertTrue(result.getContent().isEmpty());
  }

  @Test
  void shouldReturnPaginatedAdultCustomers() {
    CustomerEntity entity = new CustomerEntity();
    Customer customer = new Customer();
    Page<CustomerEntity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 20), 1);

    when(jpaCustomerRepository.getCustomersByBirthDateLessThanEqualPaginated(any(LocalDate.class), any()))
            .thenReturn(page);
    when(mapper.toCustomerList(List.of(entity))).thenReturn(List.of(customer));

    var result = adapter.getAdultCustomersPaginated(PageRequest.of(0, 20));

    assertEquals(1, result.getContent().size());
    assertSame(customer, result.getContent().getFirst());

    verify(jpaCustomerRepository).getCustomersByBirthDateLessThanEqualPaginated(any(LocalDate.class), any());
    verify(mapper).toCustomerList(List.of(entity));
    verifyNoMoreInteractions(jpaCustomerRepository, mapper);
    verifyNoInteractions(customerAccountAssembler);
  }

  @Test
  void shouldReturnEmptyPageWhenNoAdultCustomersFound() {
    Page<CustomerEntity> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

    when(jpaCustomerRepository.getCustomersByBirthDateLessThanEqualPaginated(any(LocalDate.class), any()))
            .thenReturn(emptyPage);
    when(mapper.toCustomerList(List.of())).thenReturn(List.of());

    var result = adapter.getAdultCustomersPaginated(PageRequest.of(0, 20));

    assertTrue(result.getContent().isEmpty());
  }

  @Test
  void shouldReturnPaginatedCustomersWithTotalHigherThanGivenAmount() {
    BigDecimal amount = new BigDecimal("500.00");
    CustomerEntity entity = new CustomerEntity();
    Customer customer = new Customer();
    Page<CustomerEntity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 20), 1);

    when(jpaCustomerRepository.getCustomersWithHigherAmountPaginated(eq(amount), any())).thenReturn(page);
    when(mapper.toCustomerList(List.of(entity))).thenReturn(List.of(customer));

    var result = adapter.getCustomersWithHigherAmountPaginated(amount, PageRequest.of(0, 20));

    assertEquals(1, result.getContent().size());
    assertSame(customer, result.getContent().getFirst());
    verify(jpaCustomerRepository).getCustomersWithHigherAmountPaginated(eq(amount), any());
    verify(mapper).toCustomerList(List.of(entity));
    verifyNoMoreInteractions(jpaCustomerRepository, mapper);
    verifyNoInteractions(customerAccountAssembler);
  }

  @Test
  void shouldReturnEmptyPageWhenNoCustomersExceedAmount() {
    BigDecimal amount = new BigDecimal("999999.99");
    Page<CustomerEntity> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

    when(jpaCustomerRepository.getCustomersWithHigherAmountPaginated(eq(amount), any())).thenReturn(emptyPage);
    when(mapper.toCustomerList(List.of())).thenReturn(List.of());

    var result = adapter.getCustomersWithHigherAmountPaginated(amount, PageRequest.of(0, 20));

    assertTrue(result.getContent().isEmpty());
  }

  @Test
  void shouldReturnMappedCustomerWhenFoundByDni() {
    String dni = "12345678A";
    CustomerEntity entity = new CustomerEntity();
    Customer customer = new Customer();

    when(jpaCustomerRepository.findByDni(dni)).thenReturn(Optional.of(entity));
    when(mapper.toCustomer(entity)).thenReturn(customer);

    Optional<Customer> result = adapter.findByDni(dni);

    assertTrue(result.isPresent());
    assertSame(customer, result.get());
    verify(jpaCustomerRepository).findByDni(dni);
    verify(mapper).toCustomer(entity);
    verifyNoMoreInteractions(jpaCustomerRepository, mapper);
  }

  @Test
  void shouldReturnEmptyOptionalWhenCustomerNotFoundByDni() {
    String dni = "99999999Z";

    when(jpaCustomerRepository.findByDni(dni)).thenReturn(Optional.empty());

    Optional<Customer> result = adapter.findByDni(dni);

    assertTrue(result.isEmpty());
    verify(jpaCustomerRepository).findByDni(dni);
    verifyNoInteractions(mapper);
  }

  @Test
  void shouldReturnCustomerWithAccountsWhenRowsFoundForDni() {
    String dni = "12345678A";
    CustomerAccountRow row = mock(CustomerAccountRow.class);
    Customer customer = new Customer();

    when(jpaCustomerRepository.findCustomerWithAccountsByDni(dni)).thenReturn(List.of(row));
    when(customerAccountAssembler.toCustomers(List.of(row))).thenReturn(List.of(customer));

    Optional<Customer> result = adapter.findByDniWithAccounts(dni);

    assertTrue(result.isPresent());
    assertSame(customer, result.get());
    verify(jpaCustomerRepository).findCustomerWithAccountsByDni(dni);
    verify(customerAccountAssembler).toCustomers(List.of(row));
    verifyNoMoreInteractions(jpaCustomerRepository, customerAccountAssembler);
    verifyNoInteractions(mapper);
  }

  @Test
  void shouldReturnEmptyOptionalWhenNoRowsFoundForDni() {
    String dni = "00000000X";

    when(jpaCustomerRepository.findCustomerWithAccountsByDni(dni)).thenReturn(List.of());
    when(customerAccountAssembler.toCustomers(List.of())).thenReturn(List.of());

    Optional<Customer> result = adapter.findByDniWithAccounts(dni);

    assertTrue(result.isEmpty());
    verify(jpaCustomerRepository).findCustomerWithAccountsByDni(dni);
    verify(customerAccountAssembler).toCustomers(List.of());
  }

  @Test
  void shouldSaveCustomerAndReturnMappedDomainObject() {
    Customer customer = Customer.builder()
            .dni(Dni.of("12345678A"))
            .name("Juan")
            .surname1("Perez")
            .build();

    CustomerEntity entity = new CustomerEntity();
    CustomerEntity savedEntity = new CustomerEntity();
    Customer expectedResult = Customer.builder()
            .id(1L)
            .dni(Dni.of("12345678A"))
            .name("Juan")
            .surname1("Perez")
            .build();

    when(mapper.toEntity(customer)).thenReturn(entity);
    when(jpaCustomerRepository.save(entity)).thenReturn(savedEntity);
    when(mapper.toCustomer(savedEntity)).thenReturn(expectedResult);

    Customer result = adapter.save(customer);

    assertSame(expectedResult, result);
    verify(mapper).toEntity(customer);
    verify(jpaCustomerRepository).save(entity);
    verify(mapper).toCustomer(savedEntity);
    verifyNoMoreInteractions(mapper, jpaCustomerRepository);
    verifyNoInteractions(customerAccountAssembler);
  }

  @Test
  void shouldPropagateExceptionWhenJpaSaveFailsDuringSave() {
    Customer customer = Customer.builder().dni(Dni.of("12345678A")).build();
    CustomerEntity entity = new CustomerEntity();

    when(mapper.toEntity(customer)).thenReturn(entity);
    when(jpaCustomerRepository.save(entity)).thenThrow(new RuntimeException("DB error on save"));

    org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> adapter.save(customer));
  }
}
