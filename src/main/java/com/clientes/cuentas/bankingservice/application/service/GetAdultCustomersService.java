package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.application.constants.CacheNames;
import com.clientes.cuentas.bankingservice.application.repository.CustomerRepository;
import com.clientes.cuentas.bankingservice.application.usecase.GetAdultCustomersUseCase;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for the GetAdultCustomersUseCase.
 */
@Service
@RequiredArgsConstructor
public class GetAdultCustomersService implements GetAdultCustomersUseCase {

  private final CustomerRepository customerRepository;

  @Override
  @Cacheable(value = CacheNames.ADULT_CUSTOMERS, sync = true)
  public List<Customer> execute() {
    return customerRepository.getAdultCustomers();
  }
}
