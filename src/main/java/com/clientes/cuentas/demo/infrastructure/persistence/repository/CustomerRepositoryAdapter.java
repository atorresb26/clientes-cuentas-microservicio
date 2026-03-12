package com.clientes.cuentas.demo.infrastructure.persistence.repository;

import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.domain.port.output.CustomerRepository;
import com.clientes.cuentas.demo.infrastructure.persistence.mapper.CustomerAccountProjectionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Repository implementation for the Customer Repository port.
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

  private final JpaCustomerRepository jpaCustomerRepository;
  private final CustomerAccountProjectionMapper customerAccountProjectionMapper;

  @Override
  public List<Customer> getCustomersAndAccounts() {
    log.info("- Init - getCustomersAndAccounts()");
    var customersAndAccounts = jpaCustomerRepository.getCustomersAndAccounts();
    log.debug("- getCustomersAndAccounts search returns {} results.", customersAndAccounts.size());

    // LinkedHashMap to ensure each customer is created only once and preserve the original iteration order
    Map<Long, Customer> customers = new LinkedHashMap<>();

    // Delegate the mapping between the projection row and domain objects to the Mapper
    customersAndAccounts.forEach(customerAccountRow -> {
      // If a customer with this ID exists, it returns it; otherwise, it executes the lambda (creates the customer and inserts it into the map).
      Customer customer = customers
              .computeIfAbsent(customerAccountRow.id(), id -> customerAccountProjectionMapper.toCustomer(customerAccountRow));
      if (Objects.nonNull(customerAccountRow.bankAccountId())) {
        customer.addBankAccount(customerAccountProjectionMapper.toBankAccount(customerAccountRow));
      }
    });
    log.debug("The final list of customers obtained would be: {}", customers.values());

    log.info("- End - getCustomersAndAccounts()");
    return new ArrayList<>(customers.values());
  }
}
