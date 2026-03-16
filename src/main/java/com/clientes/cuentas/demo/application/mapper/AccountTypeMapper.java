package com.clientes.cuentas.demo.application.mapper;

import com.clientes.cuentas.demo.domain.enums.AccountType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountTypeMapper {

  default AccountType fromCode(String code) {
    return AccountType.getByCode(code);
  }
}
