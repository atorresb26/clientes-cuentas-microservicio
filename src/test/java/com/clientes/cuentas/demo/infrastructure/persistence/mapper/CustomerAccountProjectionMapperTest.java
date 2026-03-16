package com.clientes.cuentas.demo.infrastructure.persistence.mapper;

import com.clientes.cuentas.demo.domain.model.BankAccount;
import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.infrastructure.persistence.projection.CustomerAccountRow;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerAccountProjectionMapperTest {

  private final CustomerAccountProjectionMapper mapper = Mappers.getMapper(CustomerAccountProjectionMapper.class);

  @Test
  void shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.toCustomer(null));
    assertNull(mapper.toBankAccount(null));
  }

  @Test
  void shouldMapCustomerFieldsAndIgnoreBankAccounts() {
    CustomerAccountRow row = mock(CustomerAccountRow.class);

    when(row.id()).thenReturn(1L);
    when(row.dni()).thenReturn("12345678A");
    when(row.name()).thenReturn("John");
    when(row.surname1()).thenReturn("Doe");
    when(row.surname2()).thenReturn("Smith");
    when(row.birthDate()).thenReturn(LocalDate.of(1990, 1, 1));

    Customer customer = mapper.toCustomer(row);

    assertEquals("12345678A", customer.getDni());
    assertEquals("John", customer.getName());
    assertEquals("Doe", customer.getSurname1());
    assertEquals("Smith", customer.getSurname2());
    assertEquals(LocalDate.of(1990, 1, 1), customer.getBirthDate());

    // It must be empty because it is ignored
    assertTrue(customer.getBankAccounts().isEmpty());
  }

  @Test
  void shouldMapBankAccountFields() {
    CustomerAccountRow row = mock(CustomerAccountRow.class);

    when(row.bankAccountType()).thenReturn("NORMAL");
    when(row.total()).thenReturn(new BigDecimal("10000.50"));

    BankAccount account = mapper.toBankAccount(row);

    assertEquals("NORMAL", account.getAccountType().getName());
    assertEquals(new BigDecimal("10000.50"), account.getTotal());
  }
}
