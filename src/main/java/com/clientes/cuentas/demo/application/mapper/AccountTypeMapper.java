package com.clientes.cuentas.demo.application.mapper;

import com.clientes.cuentas.demo.domain.enums.AccountType;
import com.clientes.cuentas.demo.infrastructure.persistence.entity.AccountTypeEntity;
import org.mapstruct.Mapper;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface AccountTypeMapper {

  default AccountType fromEntity(AccountTypeEntity entity) {
    return Objects.isNull(entity) ? null : AccountType.getByCode(entity.getCode());
  }

  default AccountType fromCode(String code) {
    return AccountType.getByCode(code);
  }
}
