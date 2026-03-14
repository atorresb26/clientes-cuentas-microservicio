package com.clientes.cuentas.demo.infrastructure.persistence.repository;

import com.clientes.cuentas.demo.infrastructure.persistence.entity.CustomerEntity;
import com.clientes.cuentas.demo.infrastructure.persistence.projection.CustomerAccountRow;
import io.micrometer.core.annotation.Timed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

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
          SELECT new com.clientes.cuentas.demo.infrastructure.persistence.projection.CustomerAccountRow(
                      c.id,
                      c.dni,
                      c.name,
                      c.surname1,
                      c.surname2,
                      c.birthDate,
                      ba.id AS bankAccountId,
                      ba.accountType.name AS bankAccountType,
                      ba.total
                      )
            FROM CustomerEntity c
            LEFT JOIN BankAccountEntity ba
            ON ba.customer.id = c.id
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
}
