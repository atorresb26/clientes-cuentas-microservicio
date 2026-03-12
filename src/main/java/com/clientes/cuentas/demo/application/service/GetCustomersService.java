package com.clientes.cuentas.demo.application.service;

import com.clientes.cuentas.demo.application.port.input.GetCustomersUseCase;
import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.domain.port.output.CustomerRepository;
import lombok.RequiredArgsConstructor;
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
  public List<Customer> getCustomers() {
    return customerRepository.getCustomersAndAccounts();
  }
}
