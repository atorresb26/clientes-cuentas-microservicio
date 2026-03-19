package com.clientes.cuentas.bankingservice.application.mapper;

import com.clientes.cuentas.bankingservice.application.command.CreateBankAccountForCustomerCommand;
import com.clientes.cuentas.bankingservice.domain.enums.AccountType;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidAmountException;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidAccountTypeCodeException;
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
    CreateBankAccountForCustomerCommand command = new CreateBankAccountForCustomerCommand(
            "12345678A",
            "NRML",
            new BigDecimal("250.00")
    );

    BankAccount result = mapper.toBankAccount(command);

    assertEquals(AccountType.NORMAL, result.getAccountType());
    assertEquals(new BigDecimal("250.00"), result.getTotal().amount());
    assertNull(result.getApiId());
    assertNull(result.getCustomerId());
  }

  @Test
  void shouldReturnNullWhenCommandIsNull() {
    assertNull(mapper.toBankAccount(null));
  }

  @Test
  void shouldThrowExceptionWhenAccountTypeCodeIsInvalid() {
    CreateBankAccountForCustomerCommand command = new CreateBankAccountForCustomerCommand(
            "12345678A",
            "XXX",
            new BigDecimal("10.00")
    );

    InvalidAccountTypeCodeException ex = assertThrows(
            InvalidAccountTypeCodeException.class,
            () -> mapper.toBankAccount(command)
    );
    assertEquals("Invalid account type code 'XXX'. Accepted values are: [JR, NRML, PREM]", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenTotalIsNull() {
    CreateBankAccountForCustomerCommand command = new CreateBankAccountForCustomerCommand(
            "12345678A",
            "NRML",
            null
    );

    InvalidAmountException ex = assertThrows(
            InvalidAmountException.class,
            () -> mapper.toBankAccount(command)
    );
    assertEquals("The amount must be greater than or equal to 0", ex.getMessage());
  }
}
