package com.clientes.cuentas.demo.infrastructure.persistence.mapper;

import com.clientes.cuentas.demo.domain.exception.AccountTypeNotFoundException;
import com.clientes.cuentas.demo.infrastructure.persistence.entity.AccountTypeEntity;
import com.clientes.cuentas.demo.infrastructure.persistence.repository.JpaAccountTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AccountTypeReferenceMapper {

  private final JpaAccountTypeRepository jpaAccountTypeRepository;

  @Cacheable(value = "account-types", key = "#code", condition = "#code != null")
  public AccountTypeEntity mapFromCode(String code) {
    if (Objects.isNull(code)) {
      return null;
    }
    return jpaAccountTypeRepository.findByCode(code)
            .orElseThrow(() -> new AccountTypeNotFoundException(code));
  }
}
