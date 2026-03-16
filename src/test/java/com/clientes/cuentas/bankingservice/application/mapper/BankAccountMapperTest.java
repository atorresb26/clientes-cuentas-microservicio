package com.clientes.cuentas.bankingservice.application.mapper;

import com.clientes.cuentas.bankingservice.application.command.CreateBankAccountForCustomerCommand;
import com.clientes.cuentas.bankingservice.domain.enums.AccountType;
import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BankAccountMapperTest {

  private final BankAccountMapper mapper = Mappers.getMapper(BankAccountMapper.class);

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(mapper, "accountTypeMapper", Mappers.getMapper(AccountTypeMapper.class));
  }

  @Test
  void shouldMapCommandToBankAccount() {
    CreateBankAccountForCustomerCommand command = new CreateBankAccountForCustomerCommand();
    command.setCustomerDni("12345678A");
    command.setAccountTypeCode("NRML");
    command.setTotal(new BigDecimal("250.00"));

    BankAccount result = mapper.toBankAccount(command);

    assertEquals(AccountType.NORMAL, result.getAccountType());
    assertEquals(new BigDecimal("250.00"), result.getTotal());
    assertNull(result.getApiId());
    assertNull(result.getCustomerId());
  }

  @Test
  void shouldReturnNullWhenCommandIsNull() {
    assertNull(mapper.toBankAccount(null));
  }

  @Test
  void shouldThrowExceptionWhenAccountTypeCodeIsInvalid() {
    CreateBankAccountForCustomerCommand command = new CreateBankAccountForCustomerCommand();
    command.setAccountTypeCode("XXX");
    command.setTotal(new BigDecimal("10.00"));

    EnumConstantNotPresentException ex = assertThrows(
            EnumConstantNotPresentException.class,
            () -> mapper.toBankAccount(command)
    );
    assertEquals("XXX", ex.constantName());
  }
}
