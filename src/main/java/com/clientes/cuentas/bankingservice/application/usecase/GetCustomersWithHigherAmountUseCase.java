package com.clientes.cuentas.bankingservice.application.usecase;

import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.application.port.dto.PaginationRequestDTO;
import com.clientes.cuentas.bankingservice.application.port.model.PageResult;

import java.math.BigDecimal;

/**
 * Use case of obtaining all customers with higher amount than the received one with pagination support.
 */
public interface GetCustomersWithHigherAmountUseCase {

  /**
   * Retrieves all customers whose total balance across all their bank accounts is greater than the specified amount,
   * with pagination support.
   *
   * @param amount the minimum total balance that the sum of all bank accounts associated with a customer must exceed
   * @param pagination the pagination parameters (page, size, sort)
   * @return a paginated result containing customers with higher balance
   */
  PageResult<Customer> execute(BigDecimal amount, PaginationRequestDTO pagination);
}
