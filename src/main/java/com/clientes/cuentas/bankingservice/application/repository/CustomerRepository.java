package com.clientes.cuentas.bankingservice.application.repository;

import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.model.Customer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Output port for the repository dedicated to the {@code cliente} table
 */
public interface CustomerRepository {

  /**
   * Retrieves all customers together with their associated bank accounts.
   *
   * @return a list of {@link Customer} domain objects, each containing the list of associated {@link BankAccount}.
   */
  List<Customer> getCustomersAndAccounts();

  /**
   * Retrieves all the adults customers (>= 18 years old).
   *
   * @return a list of {@link Customer} domain objects
   */
  List<Customer> getAdultCustomers();

  /**
   * Retrieves all customers whose total balance across all their bank accounts is greater than the specified amount.
   *
   * @param amount the amount provided to filter
   * @return the list of {@link Customer} domain objects
   */
  List<Customer> getCustomersWithHigherAmount(BigDecimal amount);

  /**
   * Finds a customer by their DNI.
   *
   * @param dni the customer's DNI
   * @return an {@link Optional} containing the matching {@link Customer}, or empty if no customer is found
   */
  Optional<Customer> findByDni(String dni);

  /**
   * Finds a customer by their DNI together with all their associated bank accounts
   *
   * @param dni the customer's DNI
   * @return an {@link Optional} containing the matching {@link Customer} with their accounts, or empty if not found
   */
  Optional<Customer> findByDniWithAccounts(String dni);

  /**
   * Persists the given customer.
   *
   * @param customer the customer to persist
   * @return the persisted customer
   */
  Customer save(Customer customer);
}
