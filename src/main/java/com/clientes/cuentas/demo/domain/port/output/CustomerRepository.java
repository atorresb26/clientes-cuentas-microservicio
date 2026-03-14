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
   * <p>
   * The underlying JPA query returns a flat result set where each row represents
   * a combination of a customer and one of their bank accounts. Therefore, a
   * customer with multiple accounts will appear in multiple rows.
   * </p>
   *
   * @return a list of {@link Customer} domain objects, each containing the list
   * of associated {@link BankAccount}.
   */
  List<Customer> getCustomersAndAccounts();
}
