package com.clientes.cuentas.bankingservice.application.mapper;

import com.clientes.cuentas.bankingservice.domain.enums.AccountType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    assertThrows(EnumConstantNotPresentException.class, () -> mapper.fromCode(null));
  }

  @Test
  void shouldThrowExceptionWhenCodeIsInvalid() {
    EnumConstantNotPresentException ex = assertThrows(
            EnumConstantNotPresentException.class,
            () -> mapper.fromCode("XXX")
    );
    assertTrue(ex.getMessage().contains("XXX"));
  }
}
