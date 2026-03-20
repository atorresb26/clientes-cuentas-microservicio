package com.clientes.cuentas.bankingservice.infrastructure.persistence.repository;

import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.AccountTypeEntity;
import io.micrometer.core.annotation.Timed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JPA repository for managing {@link AccountTypeEntity} persistence operations.
 */
public interface JpaAccountTypeRepository extends JpaRepository<AccountTypeEntity, Long> {

  /**
   * Retrieves an account type entity by its unique code.
   *
   * @param code the account type code
   * @return an {@link Optional} containing the matching entity if found, or empty otherwise
   */
  @Timed(value = "jpa.db.query", extraTags = {
          "repository", "JpaAccountTypeRepository",
          "method", "findByCode"
  })
  Optional<AccountTypeEntity> findByCode(String code);
}
