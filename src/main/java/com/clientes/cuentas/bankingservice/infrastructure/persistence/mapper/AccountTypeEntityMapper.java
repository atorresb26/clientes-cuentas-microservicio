package com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper;

import com.clientes.cuentas.bankingservice.domain.enums.AccountType;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.AccountTypeEntity;
import org.mapstruct.Mapper;

import java.util.Objects;

/**
 * Mapper interface for converting {@link AccountTypeEntity} persistence objects
 * to {@link AccountType} domain enums.
 */
@Mapper(componentModel = "spring")
public interface AccountTypeEntityMapper {

  /**
   * Converts an {@link AccountTypeEntity} persistence object to an {@link AccountType} domain enum.
   *
   * @param entity the {@link AccountTypeEntity} to convert; may be {@code null}
   * @return the corresponding {@link AccountType} enum value, or {@code null} if the entity is {@code null}
   */
  default AccountType fromEntity(AccountTypeEntity entity) {
    return Objects.isNull(entity) ? null : AccountType.getByCode(entity.getCode());
  }
}
