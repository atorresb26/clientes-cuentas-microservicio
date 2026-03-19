package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.application.constants.CacheNames;
import com.clientes.cuentas.bankingservice.application.repository.CustomerRepository;
import com.clientes.cuentas.bankingservice.application.usecase.GetCustomersUseCase;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
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
  @Cacheable(value = CacheNames.CUSTOMER_WITH_ACCOUNTS, sync = true)
  public List<Customer> execute() {
    return customerRepository.getCustomersAndAccounts();
  }
}
