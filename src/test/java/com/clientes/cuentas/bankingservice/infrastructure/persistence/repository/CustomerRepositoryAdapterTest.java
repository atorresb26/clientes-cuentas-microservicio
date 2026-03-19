package com.clientes.cuentas.bankingservice.infrastructure.persistence.repository;

import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.model.vo.Dni;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.CustomerEntity;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper.CustomerAccountAssembler;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper.CustomerEntityMapper;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.projection.CustomerAccountRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

  // -------------------------------------------------------------------------
  // getCustomersAndAccounts()
  // -------------------------------------------------------------------------

  @Test
  void shouldReturnCustomersAndAccountsAssembledFromProjectionRows() {
    CustomerAccountRow row = mock(CustomerAccountRow.class);
    Customer customer = new Customer();

    when(jpaCustomerRepository.getCustomersAndAccounts()).thenReturn(List.of(row));
    when(customerAccountAssembler.toCustomers(List.of(row))).thenReturn(List.of(customer));

    List<Customer> result = adapter.getCustomersAndAccounts();

    assertEquals(1, result.size());
    assertSame(customer, result.getFirst());
    verify(jpaCustomerRepository).getCustomersAndAccounts();
    verify(customerAccountAssembler).toCustomers(List.of(row));
    verifyNoMoreInteractions(jpaCustomerRepository, customerAccountAssembler);
    verifyNoInteractions(mapper);
  }

  @Test
  void shouldReturnEmptyListWhenNoCustomersAndAccountsExist() {
    when(jpaCustomerRepository.getCustomersAndAccounts()).thenReturn(List.of());
    when(customerAccountAssembler.toCustomers(List.of())).thenReturn(List.of());

    List<Customer> result = adapter.getCustomersAndAccounts();

    assertTrue(result.isEmpty());
  }

  // -------------------------------------------------------------------------
  // getAdultCustomers()
  // -------------------------------------------------------------------------

  @Test
  void shouldQueryWithDateEighteenYearsAgoToGetAdultCustomers() {
    CustomerEntity entity = new CustomerEntity();
    Customer customer = new Customer();
    LocalDate expectedAdultDate = LocalDate.now().minusYears(18);

    when(jpaCustomerRepository.getCustomersByBirthDateLessThanEqual(any(LocalDate.class)))
            .thenReturn(List.of(entity));
    when(mapper.toCustomerList(List.of(entity))).thenReturn(List.of(customer));

    List<Customer> result = adapter.getAdultCustomers();

    assertEquals(1, result.size());
    assertSame(customer, result.getFirst());

    ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
    verify(jpaCustomerRepository).getCustomersByBirthDateLessThanEqual(dateCaptor.capture());
    assertEquals(expectedAdultDate, dateCaptor.getValue(),
            "The query date must be exactly 18 years before today");
    verify(mapper).toCustomerList(List.of(entity));
    verifyNoMoreInteractions(jpaCustomerRepository, mapper);
    verifyNoInteractions(customerAccountAssembler);
  }

  @Test
  void shouldReturnEmptyListWhenNoAdultCustomersFound() {
    when(jpaCustomerRepository.getCustomersByBirthDateLessThanEqual(any(LocalDate.class)))
            .thenReturn(List.of());
    when(mapper.toCustomerList(List.of())).thenReturn(List.of());

    List<Customer> result = adapter.getAdultCustomers();

    assertTrue(result.isEmpty());
  }

  // -------------------------------------------------------------------------
  // getCustomersWithHigherAmount()
  // -------------------------------------------------------------------------

  @Test
  void shouldReturnCustomersWithTotalHigherThanGivenAmount() {
    BigDecimal amount = new BigDecimal("500.00");
    CustomerEntity entity = new CustomerEntity();
    Customer customer = new Customer();

    when(jpaCustomerRepository.getCustomersWithHigherAmount(amount)).thenReturn(List.of(entity));
    when(mapper.toCustomerList(List.of(entity))).thenReturn(List.of(customer));

    List<Customer> result = adapter.getCustomersWithHigherAmount(amount);

    assertEquals(1, result.size());
    assertSame(customer, result.getFirst());
    verify(jpaCustomerRepository).getCustomersWithHigherAmount(amount);
    verify(mapper).toCustomerList(List.of(entity));
    verifyNoMoreInteractions(jpaCustomerRepository, mapper);
    verifyNoInteractions(customerAccountAssembler);
  }

  @Test
  void shouldReturnEmptyListWhenNoCustomersExceedAmount() {
    BigDecimal amount = new BigDecimal("999999.99");

    when(jpaCustomerRepository.getCustomersWithHigherAmount(amount)).thenReturn(List.of());
    when(mapper.toCustomerList(List.of())).thenReturn(List.of());

    List<Customer> result = adapter.getCustomersWithHigherAmount(amount);

    assertTrue(result.isEmpty());
  }

  // -------------------------------------------------------------------------
  // findByDni()
  // -------------------------------------------------------------------------

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

  // -------------------------------------------------------------------------
  // findByDniWithAccounts()
  // -------------------------------------------------------------------------

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

  // -------------------------------------------------------------------------
  // save()
  // -------------------------------------------------------------------------

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

