package com.clientes.cuentas.bankingservice.application.mapper;

import com.clientes.cuentas.bankingservice.domain.enums.AccountType;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidAccountTypeCodeException;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountTypeMapperTest {

  private final AccountTypeMapper mapper = Mappers.getMapper(AccountTypeMapper.class);

  @Test
  void shouldMapKnownCodeToAccountType() {
    assertEquals(AccountType.JUNIOR, mapper.fromCode("JR"));
    assertEquals(AccountType.NORMAL, mapper.fromCode("NRML"));
    assertEquals(AccountType.PREMIUM, mapper.fromCode("PREM"));
  }

  @Test
  void shouldThrowExceptionWhenCodeIsNull() {
    InvalidAccountTypeCodeException ex = assertThrows(InvalidAccountTypeCodeException.class, () -> mapper.fromCode(null));
    assertEquals("Invalid account type code. Accepted values are: [JR, NRML, PREM]", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenCodeIsInvalid() {
    InvalidAccountTypeCodeException ex = assertThrows(
            InvalidAccountTypeCodeException.class,
            () -> mapper.fromCode("XXX")
    );
    assertEquals("Invalid account type code 'XXX'. Accepted values are: [JR, NRML, PREM]", ex.getMessage());
  }
}
