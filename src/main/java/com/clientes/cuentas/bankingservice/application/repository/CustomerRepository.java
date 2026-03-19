package com.clientes.cuentas.bankingservice.application.repository;

import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Output port for the repository dedicated to the {@code cliente} table
 */
public interface CustomerRepository {

  /**
   * Retrieves all customers together with their associated bank accounts with pagination support.
   *
   * @param pageable the pagination criteria
   * @return a page of {@link Customer} domain objects, each containing the list of associated {@link BankAccount}.
   */
  Page<Customer> getCustomersAndAccountsPaginated(Pageable pageable);

  /**
   * Retrieves all the adults customers (>= 18 years old) with pagination support.
   *
   * @param pageable the pagination criteria
   * @return a page of {@link Customer} domain objects
   */
  Page<Customer> getAdultCustomersPaginated(Pageable pageable);

  /**
   * Retrieves all customers whose total balance across all their bank accounts is greater than the specified amount,
   * with pagination support.
   *
   * @param amount the amount provided to filter
   * @param pageable the pagination criteria
   * @return a page of {@link Customer} domain objects
   */
  Page<Customer> getCustomersWithHigherAmountPaginated(BigDecimal amount, Pageable pageable);

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
