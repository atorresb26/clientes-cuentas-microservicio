package com.clientes.cuentas.bankingservice.infrastructure.persistence.repository;

import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.BankAccountEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JPA Repository for the BankAccount Entity.
 */
public interface JpaBankAccountRepository extends JpaRepository<BankAccountEntity, Long> {

  /**
   * Retrieves a bank account by its public API identifier.
   *
   * @param apiId the public API identifier of the bank account
   * @return an {@link Optional} containing the matching bank account entity if found, otherwise empty
   */
  @EntityGraph(attributePaths = "accountType")
  Optional<BankAccountEntity> findByApiId(String apiId);
}
