package com.clientes.cuentas.demo.infrastructure.persistence.mapper;

import com.clientes.cuentas.demo.domain.enums.AccountType;
import com.clientes.cuentas.demo.infrastructure.persistence.entity.AccountTypeEntity;
import org.mapstruct.Mapper;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface AccountTypeEntityMapper {

  default AccountType fromEntity(AccountTypeEntity entity) {
    return Objects.isNull(entity) ? null : AccountType.getByCode(entity.getCode());
  }
}
