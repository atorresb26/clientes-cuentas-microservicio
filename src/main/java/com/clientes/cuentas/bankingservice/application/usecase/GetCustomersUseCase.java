package com.clientes.cuentas.bankingservice.application.usecase;

import com.clientes.cuentas.bankingservice.domain.model.Customer;

import java.util.List;

/**
 * Use case of obtaining all customers with their associated accounts.
 */
public interface GetCustomersUseCase {

  /**
   * Obtain all customers with their associated accounts.
   *
   * @return the list of all customers.
   */
  List<Customer> execute();
}
