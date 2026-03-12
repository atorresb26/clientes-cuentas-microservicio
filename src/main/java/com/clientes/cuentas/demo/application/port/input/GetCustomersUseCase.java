package com.clientes.cuentas.demo.application.port.input;

import com.clientes.cuentas.demo.domain.model.Customer;

import java.util.List;

/**
 * Input port for the use case of obtaining all customers with their associated accounts.
 */
public interface GetCustomersUseCase {

  /**
   * Obtain all customers with their associated accounts.
   *
   * @return the list of all customers.
   */
  List<Customer> getCustomers();
}
