package com.clientes.cuentas.bankingservice.infrastructure.persistence.repository;

import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.CustomerEntity;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.projection.CustomerAccountRow;
import io.micrometer.core.annotation.Timed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for the Customer Entity.
 */
public interface JpaCustomerRepository extends JpaRepository<CustomerEntity, Long> {

  /**
   * Retrieves all customers together with their associated bank accounts mapped into a projection object
   * to avoid N+1 queries.
   *
   * @return The list of rows representing the customers with one account. If any customer have more than one,
   * then it will be in more than one record
   */
  @Timed(value = "jpa.db.query", extraTags = {
          "repository", "JpaCustomerRepository",
          "method", "getCustomersAndAccounts"
  })
  @Query("""
          SELECT new com.clientes.cuentas.bankingservice.infrastructure.persistence.projection.CustomerAccountRow(
                      c.id,
                      c.dni,
                      c.name,
                      c.surname1,
                      c.surname2,
                      c.birthDate,
                      ba.id AS bankAccountId,
                      ba.apiId AS bankAccountApiId,
                      at.name AS bankAccountType,
                      ba.total
                      )
            FROM CustomerEntity c
            LEFT JOIN BankAccountEntity ba ON ba.customer.id = c.id
            LEFT JOIN ba.accountType at
          """)
  List<CustomerAccountRow> getCustomersAndAccounts();

  /**
   * Retrieves a list of customers who meet the condition of having been born before the date specified as a parameter.
   *
   * @param date the date we want to use for the search
   * @return list of customers who meet the condition
   */
  @Timed(value = "jpa.db.query", extraTags = {
          "repository", "JpaCustomerRepository",
          "method", "getCustomersByBirthDateBefore"
  })
  List<CustomerEntity> getCustomersByBirthDateLessThanEqual(LocalDate date);

  /**
   * Retrieves all customers whose total balance across all their bank accounts
   * is greater than the specified amount.
   *
   * <p>The query joins customers with their associated bank accounts and groups
   * the results by customer. For each customer, the total balance is calculated
   * using the {@code SUM} aggregate function. Only customers whose aggregated
   * balance exceeds the given amount are returned.</p>
   *
   * @param amount the minimum total balance that the sum of all bank accounts
   *               associated with a customer must exceed
   * @return a list of {@link CustomerEntity} whose aggregated account balance
   * is greater than the specified amount
   */
  @Timed(value = "jpa.db.query", extraTags = {
          "repository", "JpaCustomerRepository",
          "method", "getCustomersWithHigherAmount"
  })
  @Query("""
          SELECT c
          FROM CustomerEntity c
          JOIN BankAccountEntity ba ON ba.customer.id = c.id
          GROUP BY c
          HAVING SUM(ba.total) > :amount
          """)
  List<CustomerEntity> getCustomersWithHigherAmount(@Param("amount") BigDecimal amount);


  /**
   * Finds a customer by their unique DNI.
   *
   * @param dni the DNI to search for
   * @return an {@code Optional} containing the matching customer if found, or empty if no customer matches the DNI
   */
  @Timed(value = "jpa.db.query", extraTags = {
          "repository", "JpaCustomerRepository",
          "method", "findByDni"
  })
  Optional<CustomerEntity> findByDni(String dni);

  /**
   * Retrieves a customer together with all their associated bank accounts.
   *
   * @param dni the customer's DNI
   * @return the list of flat rows for the matching customer
   */
  @Timed(value = "jpa.db.query", extraTags = {
          "repository", "JpaCustomerRepository",
          "method", "findCustomerWithAccountsByDni"
  })
  @Query("""
          SELECT new com.clientes.cuentas.bankingservice.infrastructure.persistence.projection.CustomerAccountRow(
                      c.id,
                      c.dni,
                      c.name,
                      c.surname1,
                      c.surname2,
                      c.birthDate,
                      ba.id,
                      ba.apiId,
                      at.name,
                      ba.total
                      )
            FROM CustomerEntity c
            LEFT JOIN BankAccountEntity ba ON ba.customer.id = c.id
            LEFT JOIN ba.accountType at
            WHERE c.dni = :dni
          """)
  List<CustomerAccountRow> findCustomerWithAccountsByDni(@Param("dni") String dni);
}
