package com.clientes.cuentas.bankingservice.application.usecase;

import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.application.pagination.PaginationCriteria;
import com.clientes.cuentas.bankingservice.application.pagination.PageResult;

/**
 * Use case of obtaining all customers with their associated accounts with pagination support.
 */
public interface GetCustomersUseCase {

  /**
   * Obtain all customers with their associated accounts with pagination support.
   *
   * @param pagination the pagination parameters (page, size, sort)
   * @return a paginated result containing customers
   */
  PageResult<Customer> execute(PaginationCriteria pagination);
}
