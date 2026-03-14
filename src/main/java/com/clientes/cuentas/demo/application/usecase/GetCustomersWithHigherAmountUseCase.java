package com.clientes.cuentas.demo.application.usecase;

import com.clientes.cuentas.demo.domain.model.Customer;

import java.util.List;

/**
 * Use case of obtaining all customers with higher amount than the received one.
 */
public interface GetCustomersWithHigherAmountUseCase {

  /**
   * Retrieves all customers whose total balance across all their bank accounts is greater than the specified amount.
   *
   * @param amount the minimum total balance that the sum of all bank accounts associated with a customer must exceed
   * @return a list of {@link Customer} whose aggregated account balance is greater than the specified amount
   */
  List<Customer> execute(Double amount);
}
