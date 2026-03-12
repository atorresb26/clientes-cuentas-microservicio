package com.clientes.cuentas.demo.infrastructure.persistence.repository;

import com.clientes.cuentas.demo.domain.model.BankAccount;
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

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

  private final JpaCustomerRepository jpaCustomerRepository;
  private final CustomerAccountProjectionMapper customerAccountProjectionMapper;

  /**
   * Retrieves all customers together with their associated bank accounts.
   *
   * <p>
   * The underlying JPA query returns a flat result set where each row represents
   * a combination of a customer and one of their bank accounts. Therefore, a
   * customer with multiple accounts will appear in multiple rows.
   * </p>
   *
   * <p>
   * This method reconstructs the domain aggregate {@link Customer} by grouping
   * the rows by customer identifier and attaching the corresponding
   * {@link BankAccount} objects to each customer.
   * </p>
   *
   * @return a list of {@link Customer} domain objects, each containing the list
   * of associated {@link BankAccount}.
   */
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
