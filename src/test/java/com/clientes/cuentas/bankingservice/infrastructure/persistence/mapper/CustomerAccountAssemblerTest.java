package com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper;

import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.projection.CustomerAccountRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerAccountAssemblerTest {

  @InjectMocks
  private CustomerAccountAssembler assembler;

  @Mock
  private CustomerAccountProjectionMapper mapper;

  @Test
  void shouldReturnEmptyListWhenRowsAreEmpty() {
    List<Customer> result = assembler.toCustomers(List.of());

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldCreateSingleCustomerWhenOneRowExists() {
    CustomerAccountRow row = mock(CustomerAccountRow.class);
    Customer customer = new Customer();

    when(row.id()).thenReturn(1L);
    when(row.bankAccountId()).thenReturn(null);
    when(mapper.toCustomer(row)).thenReturn(customer);

    List<Customer> result = assembler.toCustomers(List.of(row));

    assertEquals(1, result.size());
    assertSame(customer, result.getFirst());
    verify(mapper).toCustomer(row);
  }

  @Test
  void shouldAddBankAccountWhenBankAccountIdExists() {
    CustomerAccountRow row = mock(CustomerAccountRow.class);
    Customer customer = new Customer();
    BankAccount account = new BankAccount();

    when(row.id()).thenReturn(1L);
    when(row.bankAccountId()).thenReturn(10L);
    when(mapper.toCustomer(row)).thenReturn(customer);
    when(mapper.toBankAccount(row)).thenReturn(account);

    List<Customer> result = assembler.toCustomers(List.of(row));

    assertEquals(1, result.size());
    assertEquals(1, result.getFirst().getBankAccounts().size());
    assertEquals(account, result.getFirst().getBankAccounts().getFirst());
  }

  @Test
  void shouldGroupRowsByCustomerId() {
    CustomerAccountRow row1 = mock(CustomerAccountRow.class);
    CustomerAccountRow row2 = mock(CustomerAccountRow.class);

    Customer customer = new Customer();
    BankAccount acc1 = new BankAccount();
    BankAccount acc2 = new BankAccount();

    when(row1.id()).thenReturn(1L);
    when(row2.id()).thenReturn(1L);

    when(row1.bankAccountId()).thenReturn(10L);
    when(row2.bankAccountId()).thenReturn(11L);

    when(mapper.toCustomer(row1)).thenReturn(customer);
    when(mapper.toBankAccount(row1)).thenReturn(acc1);
    when(mapper.toBankAccount(row2)).thenReturn(acc2);

    List<Customer> result = assembler.toCustomers(List.of(row1, row2));

    assertEquals(1, result.size());
    assertEquals(2, result.getFirst().getBankAccounts().size());
  }

  @Test
  void shouldCreateMultipleCustomersWhenIdsAreDifferent() {
    CustomerAccountRow row1 = mock(CustomerAccountRow.class);
    CustomerAccountRow row2 = mock(CustomerAccountRow.class);

    Customer customer1 = new Customer();
    Customer customer2 = new Customer();

    when(row1.id()).thenReturn(1L);
    when(row2.id()).thenReturn(2L);

    when(row1.bankAccountId()).thenReturn(null);
    when(row2.bankAccountId()).thenReturn(null);

    when(mapper.toCustomer(row1)).thenReturn(customer1);
    when(mapper.toCustomer(row2)).thenReturn(customer2);

    List<Customer> result = assembler.toCustomers(List.of(row1, row2));

    assertEquals(2, result.size());
    assertTrue(result.contains(customer1));
    assertTrue(result.contains(customer2));
  }
}
