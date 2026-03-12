package com.clientes.cuentas.demo.domain.port.output;

import com.clientes.cuentas.demo.domain.model.Customer;

import java.util.List;

/**
 * Output port for the repository dedicated to the {@code cliente} table
 */
public interface CustomerRepository {

  /**
   * Obtain the list of customers with their associated accounts from the database.
   *
   * @return the list of customers.
   */
  List<Customer> getCustomersAndAccounts();
}
