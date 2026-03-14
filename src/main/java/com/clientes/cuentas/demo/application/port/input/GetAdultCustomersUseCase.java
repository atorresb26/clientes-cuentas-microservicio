package com.clientes.cuentas.demo.application.port.input;

import com.clientes.cuentas.demo.domain.model.Customer;

import java.util.List;

/**
 * Input port for the use case of obtaining all adult customers.
 */
public interface GetAdultCustomersUseCase {

  /**
   * Obtain all adult customers (>= 18 years old).
   *
   * @return the list of customers.
   */
  List<Customer> getAdultCustomers();
}
