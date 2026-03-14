package com.clientes.cuentas.demo.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerTest {

  @Test
  void shouldInitializeBankAccountListWhenNull() {
    Customer customer = new Customer();
    BankAccount account = new BankAccount();

    customer.addBankAccount(account);

    assertNotNull(customer.getBankAccounts());
    assertEquals(1, customer.getBankAccounts().size());
  }

  @Test
  void shouldAddBankAccountToExistingList() {
    Customer customer = new Customer();

    BankAccount account1 = new BankAccount();
    BankAccount account2 = new BankAccount();

    customer.addBankAccount(account1);
    customer.addBankAccount(account2);

    List<BankAccount> accounts = customer.getBankAccounts();

    assertEquals(2, accounts.size());
    assertTrue(accounts.contains(account1));
    assertTrue(accounts.contains(account2));
  }
}
