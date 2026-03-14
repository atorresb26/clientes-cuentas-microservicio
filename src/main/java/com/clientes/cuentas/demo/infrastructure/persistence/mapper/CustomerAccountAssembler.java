package com.clientes.cuentas.demo.infrastructure.persistence.mapper;

import com.clientes.cuentas.demo.domain.model.BankAccount;
import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.infrastructure.persistence.projection.CustomerAccountRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *  Convert the rows of the projection to a domain-specific object for customer
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerAccountAssembler {

  private final CustomerAccountProjectionMapper mapper;

  /**
   * This method reconstructs the domain aggregate {@link Customer} by grouping
   * the rows by customer identifier and attaching the corresponding
   * {@link BankAccount} objects to each customer.
   *
   * @param customerAccountRows List of projection objects containing all customers and their associated accounts
   * @return the list of customers and their accounts
   */
  public List<Customer> toCustomers(List<CustomerAccountRow> customerAccountRows) {
    // LinkedHashMap to ensure each customer is created only once and preserve the original iteration order
    Map<Long, Customer> customers = new LinkedHashMap<>();

    // Delegate the mapping between the projection row and domain objects to the Mapper
    customerAccountRows.forEach(customerAccountRow -> {
      // If a customer with this ID exists, it returns it; otherwise, it executes the lambda (creates the customer and inserts it into the map).
      Customer customer = customers.computeIfAbsent(customerAccountRow.id(), id -> mapper.toCustomer(customerAccountRow));

      if (Objects.nonNull(customerAccountRow.bankAccountId())) {
        customer.addBankAccount(mapper.toBankAccount(customerAccountRow));
      }
    });
    log.debug("The final list of customers obtained would be: {}", customers.values());
    return new ArrayList<>(customers.values());
  }
}
