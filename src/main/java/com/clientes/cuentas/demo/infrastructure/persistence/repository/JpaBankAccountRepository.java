package com.clientes.cuentas.demo.infrastructure.persistence.repository;

import com.clientes.cuentas.demo.infrastructure.persistence.entity.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA Repository for the BankAccount Entity.
 */
public interface JpaBankAccountRepository extends JpaRepository<BankAccountEntity, Long> {
}
