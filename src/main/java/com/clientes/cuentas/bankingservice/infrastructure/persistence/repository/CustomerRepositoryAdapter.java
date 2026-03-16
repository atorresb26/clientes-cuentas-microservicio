package com.clientes.cuentas.bankingservice.infrastructure.persistence.repository;

import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.port.output.CustomerRepository;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper.CustomerAccountAssembler;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper.CustomerEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository implementation for the Customer Repository port.
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

  private final JpaCustomerRepository jpaCustomerRepository;
  private final CustomerAccountAssembler customerAccountAssembler;

  private final CustomerEntityMapper mapper;

  @Override
  @Transactional(readOnly = true)
  public List<Customer> getCustomersAndAccounts() {
    var customersAndAccounts = jpaCustomerRepository.getCustomersAndAccounts();
    log.debug("- getCustomersAndAccounts search returns {} results.", customersAndAccounts.size());
    return customerAccountAssembler.toCustomers(customersAndAccounts);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Customer> getAdultCustomers() {
    var adultDate = LocalDate.now().minusYears(18);
    var adultCustomers = jpaCustomerRepository.getCustomersByBirthDateLessThanEqual(adultDate);
    log.debug("- getCustomersByBirthDateBefore search returns {} results who were born before that date: {}",
            adultCustomers.size(), adultDate);
    return mapper.toCustomerList(adultCustomers);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Customer> getCustomersWithHigherAmount(BigDecimal amount) {
    var customers = jpaCustomerRepository.getCustomersWithHigherAmount(amount);
    log.debug("- getCustomersWithHigherAmount search returns {} results.", customers.size());
    return mapper.toCustomerList(customers);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Customer> findByDni(String dni) {
    log.debug("- findByDni - Searching for customer with dni {}", dni);
    return jpaCustomerRepository.findByDni(dni)
            .map(mapper::toCustomer);
  }

  @Override
  public Customer save(Customer customer) {
    var customerEntity = mapper.toEntity(customer);
    log.debug("Saving customer entity with the following data: {}", customerEntity);
    var savedEntity = jpaCustomerRepository.save(customerEntity);
    log.debug("The customer was successfully saved. Saved entity data: {}", savedEntity);
    return mapper.toCustomer(savedEntity);
  }
}
