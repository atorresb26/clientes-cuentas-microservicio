package com.clientes.cuentas.bankingservice.application.usecase;

import com.clientes.cuentas.bankingservice.domain.model.Customer;

/**
 * Use case contract for retrieving a customer by DNI.
 */
public interface GetCustomerByDniUseCase {

  /**
   * Executes the use case to retrieve a customer by DNI.
   *
   * @param dni the customer's DNI
   * @return the customer associated with the provided DNI
   */
  Customer execute(String dni);
}
