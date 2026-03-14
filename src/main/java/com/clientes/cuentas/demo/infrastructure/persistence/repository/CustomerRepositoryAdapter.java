package com.clientes.cuentas.demo.infrastructure.persistence.repository;

import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.domain.port.output.CustomerRepository;
import com.clientes.cuentas.demo.infrastructure.persistence.mapper.CustomerAccountAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository implementation for the Customer Repository port.
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

  private final JpaCustomerRepository jpaCustomerRepository;
  private final CustomerAccountAssembler  customerAccountAssembler;

  @Override
  public List<Customer> getCustomersAndAccounts() {
    var customersAndAccounts = jpaCustomerRepository.getCustomersAndAccounts();
    log.debug("- getCustomersAndAccounts search returns {} results.", customersAndAccounts.size());
    return customerAccountAssembler.toCustomers(customersAndAccounts);
  }
}
