package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.application.constants.CacheNames;
import com.clientes.cuentas.bankingservice.application.repository.CustomerRepository;
import com.clientes.cuentas.bankingservice.application.usecase.GetCustomerByDniUseCase;
import com.clientes.cuentas.bankingservice.domain.exception.CustomerNotFoundException;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Implements the GetCustomerByDniUseCase to retrieve a customer by their DNI.
 */
@Service
@RequiredArgsConstructor
public class GetCustomerByDniService implements GetCustomerByDniUseCase {

  private static final String NOT_FOUND = "Customer not found for DNI %s";

  private final CustomerRepository customerRepository;

  @Override
  @Cacheable(value = CacheNames.CUSTOMER_BY_DNI, key = "#dni")
  public Customer execute(String dni) {
    if (Objects.isNull(dni)) {
      throw new IllegalArgumentException("DNI must not be null");
    }
    return customerRepository.findByDniWithAccounts(dni)
            .orElseThrow(() -> new CustomerNotFoundException(String.format(NOT_FOUND, dni)));
  }
}
