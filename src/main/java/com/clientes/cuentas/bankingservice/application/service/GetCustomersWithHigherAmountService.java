package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.application.repository.CustomerRepository;
import com.clientes.cuentas.bankingservice.application.usecase.GetCustomersWithHigherAmountUseCase;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidAmountException;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
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
  public List<Customer> execute(BigDecimal amount) {
    if (Objects.isNull(amount) || amount.compareTo(BigDecimal.ZERO) < 0) {
      log.error("Invalid amount provided, should be greater or equals than zero. Amount value: {}", amount);
      throw new InvalidAmountException("The amount must be greater than or equal to 0");
    }
    return customerRepository.getCustomersWithHigherAmount(amount);
  }
}
