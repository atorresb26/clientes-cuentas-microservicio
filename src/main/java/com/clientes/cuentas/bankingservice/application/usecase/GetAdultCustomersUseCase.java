package com.clientes.cuentas.bankingservice.application.usecase;

import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.application.pagination.PaginationCriteria;
import com.clientes.cuentas.bankingservice.application.pagination.PageResult;

/**
 * Use case of obtaining all adult customers with pagination support.
 */
public interface GetAdultCustomersUseCase {

  /**
   * Obtain all adult customers (>= 18 years old) with pagination support.
   *
   * @param pagination the pagination parameters (page, size, sort)
   * @return a paginated result containing adult customers
   */
  PageResult<Customer> execute(PaginationCriteria pagination);
}
