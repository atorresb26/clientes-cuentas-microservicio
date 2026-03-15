package com.clientes.cuentas.demo.infrastructure.persistence.repository;

import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.domain.port.output.CustomerRepository;
import com.clientes.cuentas.demo.infrastructure.persistence.mapper.CustomerAccountAssembler;
import com.clientes.cuentas.demo.infrastructure.persistence.mapper.CustomerEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

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
  public List<Customer> getCustomersAndAccounts() {
    var customersAndAccounts = jpaCustomerRepository.getCustomersAndAccounts();
    log.debug("- getCustomersAndAccounts search returns {} results.", customersAndAccounts.size());
    return customerAccountAssembler.toCustomers(customersAndAccounts);
  }

  @Override
  public List<Customer> getAdultCustomers() {
    var adultDate =  LocalDate.now().minusYears(18);
    var adultCustomers = jpaCustomerRepository.getCustomersByBirthDateLessThanEqual(adultDate);
    log.debug("- getCustomersByBirthDateBefore search returns {} results who were born before that date: {}",
            adultCustomers.size(), adultDate);
    return mapper.toCustomerList(adultCustomers);
  }

  @Override
  public List<Customer> getCustomersWithHigherAmount(Double amount) {
    var customers = jpaCustomerRepository.getCustomersWithHigherAmount(amount);
    log.debug("- getCustomersWithHigherAmount search returns {} results.", customers.size());
    return mapper.toCustomerList(customers);
  }

  @Override
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
