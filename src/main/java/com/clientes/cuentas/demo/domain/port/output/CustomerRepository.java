package com.clientes.cuentas.demo.domain.port.output;

import com.clientes.cuentas.demo.domain.model.BankAccount;
import com.clientes.cuentas.demo.domain.model.Customer;

import java.util.List;

/**
 * Output port for the repository dedicated to the {@code cliente} table
 */
public interface CustomerRepository {

  /**
   * Retrieves all customers together with their associated bank accounts.
   *
   * @return a list of {@link Customer} domain objects, each containing the list of associated {@link BankAccount}.
   */
  List<Customer> getCustomersAndAccounts();

  /**
   * Retrieves all the adults customers (>= 18 years old).
   *
   * @return a list of {@link Customer} domain objects
   */
  List<Customer> getAdultCustomers();

  /**
   * Retrieves all customers whose total balance across all their bank accounts is greater than the specified amount.
   *
   * @param amount the amount provided to filter
   * @return the list of {@link Customer} domain objects
   */
  List<Customer> getCustomersWithHigherAmount(Double amount);
}
