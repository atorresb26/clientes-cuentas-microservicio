package com.clientes.cuentas.demo.infrastructure.persistence.repository;

import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.domain.port.output.CustomerRepository;
import com.clientes.cuentas.demo.infrastructure.persistence.mapper.CustomerAccountProjectionMapper;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

  private final JpaCustomerRepository jpaCustomerRepository;
  private final CustomerAccountProjectionMapper customerAccountProjectionMapper;

  @Override
  public List<Customer> getCustomersAndAccounts() {
    var customersAndAccounts = jpaCustomerRepository.getCustomersAndAccounts();

    // LinkedHashMap to ensure each customer is created only once and preserve the original iteration order
    Map<Long, Customer> customers = new LinkedHashMap<>();

    // Delegate the mapping between the projection row and domain objects to the Mapper
    customersAndAccounts.forEach(customerAccountRow -> {
      // If a customer with this ID exists, it returns it; otherwise, it executes the lambda (creates the customer and inserts it into the map).
      Customer customer = customers
              .computeIfAbsent(customerAccountRow.id(), id -> customerAccountProjectionMapper.toCustomer(customerAccountRow));
      if (Objects.nonNull(customerAccountRow.bankAccountId())) {
        // TODO revisar buenas practicas
        if (Objects.isNull(customer.getBankAccounts())) {
          customer.setBankAccounts(new ArrayList<>());
        }
        customer.getBankAccounts().add(customerAccountProjectionMapper.toBankAccount(customerAccountRow));
      }
    });

    return new ArrayList<>(customers.values());
  }
}
