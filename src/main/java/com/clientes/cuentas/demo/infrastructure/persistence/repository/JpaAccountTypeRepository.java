package com.clientes.cuentas.demo.infrastructure.persistence.repository;

import com.clientes.cuentas.demo.infrastructure.persistence.entity.AccountTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaAccountTypeRepository extends JpaRepository<AccountTypeEntity, Long> {

  Optional<AccountTypeEntity> findByCode(String code);
}
