package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.application.repository.CustomerRepository;
import com.clientes.cuentas.bankingservice.application.usecase.GetCustomersWithHigherAmountUseCase;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidAmountException;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.application.pagination.PaginationCriteria;
import com.clientes.cuentas.bankingservice.application.pagination.PageResult;
import com.clientes.cuentas.bankingservice.application.pagination.PageResultConverter;
import com.clientes.cuentas.bankingservice.application.pagination.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Implements the use case to retrieve customers with a total amount greater than a specified value.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GetCustomersWithHigherAmountService implements GetCustomersWithHigherAmountUseCase {

  private final CustomerRepository customerRepository;

  @Override
  public PageResult<Customer> execute(BigDecimal amount, PaginationCriteria pagination) {
    if (Objects.isNull(amount) || amount.compareTo(BigDecimal.ZERO) < 0) {
      log.error("Invalid amount provided, should be greater or equals than zero. Amount value: {}", amount);
      throw new InvalidAmountException("The amount must be greater than or equal to 0");
    }
    Pageable pageable = PaginationUtils.toPageable(pagination);
    Page<Customer> customersWithHigherAmountPaginated = customerRepository
            .getCustomersWithHigherAmountPaginated(amount, pageable);
    return PageResultConverter.fromPage(customersWithHigherAmountPaginated);
  }
}
