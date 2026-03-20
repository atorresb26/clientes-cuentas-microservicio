package com.clientes.cuentas.bankingservice.application.mapper;

import com.clientes.cuentas.bankingservice.domain.enums.AccountType;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidAccountTypeCodeException;
import org.mapstruct.Mapper;

/**
 * Mapper for converting account type codes into {@link AccountType} values.
 */
@Mapper(componentModel = "spring")
public interface AccountTypeMapper {

  /**
   * Converts an account type code into the corresponding {@link AccountType} enum value.
   *
   * @param code the account type code to convert
   * @return the corresponding {@link AccountType} enum value
   * @throws InvalidAccountTypeCodeException if the provided code does not match any known account type
   */
  default AccountType fromCode(String code) {
    return AccountType.getByCode(code);
  }
}
