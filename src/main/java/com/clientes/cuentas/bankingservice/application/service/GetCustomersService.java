package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.application.usecase.GetCustomersUseCase;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.port.output.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for the GetCustomersUseCase.
 */
@Service
@RequiredArgsConstructor
public class GetCustomersService implements GetCustomersUseCase {

  private final CustomerRepository customerRepository;

  @Override
  @Cacheable(value = "customer-with-accounts", sync = true)
  public List<Customer> execute() {
    return customerRepository.getCustomersAndAccounts();
  }
}
