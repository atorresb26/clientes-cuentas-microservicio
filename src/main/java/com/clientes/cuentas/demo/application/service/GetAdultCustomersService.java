package com.clientes.cuentas.demo.application.service;

import com.clientes.cuentas.demo.application.usecase.GetAdultCustomersUseCase;
import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.domain.port.output.CustomerRepository;
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
  @Cacheable(value = "adult-customers", sync = true)
  public List<Customer> execute() {
    return customerRepository.getAdultCustomers();
  }
}
