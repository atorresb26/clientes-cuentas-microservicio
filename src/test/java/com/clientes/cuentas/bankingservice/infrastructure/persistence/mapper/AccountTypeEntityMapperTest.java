package com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper;

import com.clientes.cuentas.bankingservice.domain.enums.AccountType;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidAccountTypeCodeException;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.AccountTypeEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountTypeEntityMapperTest {

  private final AccountTypeEntityMapper mapper = Mappers.getMapper(AccountTypeEntityMapper.class);


  @Test
  void shouldReturnNullWhenEntityIsNull() {
    assertNull(mapper.fromEntity(null));
  }

  @Test
  void shouldMapJrCodeToJuniorAccountType() {
    AccountTypeEntity entity = new AccountTypeEntity();
    entity.setCode("JR");

    assertEquals(AccountType.JUNIOR, mapper.fromEntity(entity));
  }

  @Test
  void shouldMapNrmlCodeToNormalAccountType() {
    AccountTypeEntity entity = new AccountTypeEntity();
    entity.setCode("NRML");

    assertEquals(AccountType.NORMAL, mapper.fromEntity(entity));
  }

  @Test
  void shouldMapPremCodeToPremiumAccountType() {
    AccountTypeEntity entity = new AccountTypeEntity();
    entity.setCode("PREM");

    assertEquals(AccountType.PREMIUM, mapper.fromEntity(entity));
  }

  @Test
  void shouldThrowInvalidAccountTypeCodeExceptionWhenEntityHasUnknownCode() {
    AccountTypeEntity entity = new AccountTypeEntity();
    entity.setCode("UNKNOWN");

    assertThrows(InvalidAccountTypeCodeException.class, () -> mapper.fromEntity(entity));
  }
}
