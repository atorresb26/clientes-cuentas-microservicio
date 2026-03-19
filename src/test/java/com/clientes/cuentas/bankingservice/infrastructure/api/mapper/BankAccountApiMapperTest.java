package com.clientes.cuentas.bankingservice.infrastructure.api.mapper;

import com.clientes.cuentas.bankingservice.application.command.CreateBankAccountForCustomerCommand;
import com.clientes.cuentas.bankingservice.domain.enums.AccountType;
import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.model.vo.Money;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.BankAccountDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.BankAccountNoCustomerDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.CreateBankAccountForCustomerRequestDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class BankAccountApiMapperTest {

  private final BankAccountApiMapper mapper = Mappers.getMapper(BankAccountApiMapper.class);

  // =========================================================================
  // toCommand()
  // =========================================================================

  @Test
  void shouldReturnNullWhenRequestDtoIsNull() {
    assertNull(mapper.toCommand(null));
  }

  @Test
  void shouldMapDniClienteToCustomerDniAndCodTipoCuentaToAccountTypeCode() {
    CreateBankAccountForCustomerRequestDTO requestDTO = new CreateBankAccountForCustomerRequestDTO();
    requestDTO.setDniCliente("12345678A");
    requestDTO.setCodTipoCuenta(CreateBankAccountForCustomerRequestDTO.CodTipoCuentaEnum.NRML);
    requestDTO.setTotal(new BigDecimal("350.00"));

    CreateBankAccountForCustomerCommand command = mapper.toCommand(requestDTO);

    assertNotNull(command);
    // dniCliente → customerDni (renamed field)
    assertEquals("12345678A", command.customerDni());
    // codTipoCuenta → accountTypeCode mapped using .name() of the enum constant
    assertEquals("NRML", command.accountTypeCode());
    assertEquals(new BigDecimal("350.00"), command.total());
  }

  @Test
  void shouldMapEveryValidCodTipoCuentaToItsEnumConstantName() {
    record Case(CreateBankAccountForCustomerRequestDTO.CodTipoCuentaEnum input, String expectedCode) {}
    var cases = new Case[]{
            new Case(CreateBankAccountForCustomerRequestDTO.CodTipoCuentaEnum.JR,   "JR"),
            new Case(CreateBankAccountForCustomerRequestDTO.CodTipoCuentaEnum.NRML, "NRML"),
            new Case(CreateBankAccountForCustomerRequestDTO.CodTipoCuentaEnum.PREM, "PREM")
    };

    for (Case c : cases) {
      CreateBankAccountForCustomerRequestDTO requestDTO = new CreateBankAccountForCustomerRequestDTO();
      requestDTO.setCodTipoCuenta(c.input());

      CreateBankAccountForCustomerCommand command = mapper.toCommand(requestDTO);

      assertEquals(c.expectedCode(), command.accountTypeCode(),
              "Expected accountTypeCode '%s' for enum constant %s".formatted(c.expectedCode(), c.input()));
    }
  }

  @Test
  void shouldSetNullAccountTypeCodeWhenCodTipoCuentaIsNull() {
    CreateBankAccountForCustomerRequestDTO requestDTO = new CreateBankAccountForCustomerRequestDTO();
    requestDTO.setDniCliente("00000000T");
    requestDTO.setCodTipoCuenta(null);
    requestDTO.setTotal(new BigDecimal("0.00"));

    CreateBankAccountForCustomerCommand command = mapper.toCommand(requestDTO);

    assertNotNull(command);
    assertEquals("00000000T", command.customerDni());
    assertNull(command.accountTypeCode());
  }

  @Test
  void shouldReturnNullWhenBankAccountIsNullForToNoCustomerDto() {
    assertNull(mapper.toNoCustomerDto(null));
  }

  @Test
  void shouldMapBankAccountToNoCustomerDtoWithAllFields() {
    UUID apiId = UUID.randomUUID();
    BankAccount bankAccount = BankAccount.builder()
            .apiId(apiId.toString())
            .accountType(AccountType.NORMAL)
            .total(new Money(new BigDecimal("500.00")))
            .build();

    BankAccountNoCustomerDTO dto = mapper.toNoCustomerDto(bankAccount);

    assertNotNull(dto);
    // apiId String → UUID
    assertEquals(apiId, dto.getApiId());
    // accountType enum name() → String (enum constant name, e.g. "NORMAL")
    assertEquals(AccountType.NORMAL.name(), dto.getAccountType());
    assertEquals(new BigDecimal("500.00"), dto.getTotal());
  }

  @Test
  void shouldMapEveryAccountTypeEnumNameToNoCustomerDto() {
    for (AccountType type : AccountType.values()) {
      BankAccount bankAccount = BankAccount.builder()
              .apiId(UUID.randomUUID().toString())
              .accountType(type)
              .total(new Money(BigDecimal.TEN))
              .build();

      BankAccountNoCustomerDTO dto = mapper.toNoCustomerDto(bankAccount);

      assertEquals(type.name(), dto.getAccountType(),
              "Expected accountType '%s' for enum constant %s".formatted(type.name(), type));
    }
  }

  @Test
  void shouldSetNullApiIdWhenApiIdStringIsNullInNoCustomerDto() {
    BankAccount bankAccount = BankAccount.builder()
            .apiId(null)
            .accountType(AccountType.JUNIOR)
            .total(new Money(new BigDecimal("100.00")))
            .build();

    BankAccountNoCustomerDTO dto = mapper.toNoCustomerDto(bankAccount);

    assertNotNull(dto);
    assertNull(dto.getApiId());
  }

  @Test
  void shouldSetNullAccountTypeWhenAccountTypeIsNullInNoCustomerDto() {
    BankAccount bankAccount = BankAccount.builder()
            .apiId(UUID.randomUUID().toString())
            .accountType(null)
            .total(new Money(new BigDecimal("200.00")))
            .build();

    BankAccountNoCustomerDTO dto = mapper.toNoCustomerDto(bankAccount);

    assertNotNull(dto);
    assertNull(dto.getAccountType());
  }

  // =========================================================================
  // toDto()
  // =========================================================================

  @Test
  void shouldReturnNullWhenBankAccountIsNullForToDto() {
    assertNull(mapper.toDto(null));
  }

  @Test
  void shouldMapBankAccountToDtoWithAllFieldsIncludingCustomerDni() {
    UUID apiId = UUID.randomUUID();
    BankAccount bankAccount = BankAccount.builder()
            .apiId(apiId.toString())
            .accountType(AccountType.PREMIUM)
            .total(new Money(new BigDecimal("1500.00")))
            .customerDni("98765432B")
            .build();

    BankAccountDTO dto = mapper.toDto(bankAccount);

    assertNotNull(dto);
    assertEquals(apiId, dto.getApiId());
    assertEquals(AccountType.PREMIUM.name(), dto.getAccountType());
    assertEquals(new BigDecimal("1500.00"), dto.getTotal());
    // customerDni is present in toDto() but absent in toNoCustomerDto()
    assertEquals("98765432B", dto.getCustomerDni());
  }

  @Test
  void shouldSetNullApiIdWhenApiIdStringIsNullInDto() {
    BankAccount bankAccount = BankAccount.builder()
            .apiId(null)
            .accountType(AccountType.NORMAL)
            .total(new Money(BigDecimal.ZERO))
            .customerDni("12345678A")
            .build();

    BankAccountDTO dto = mapper.toDto(bankAccount);

    assertNotNull(dto);
    assertNull(dto.getApiId());
  }

  @Test
  void shouldSetNullAccountTypeWhenAccountTypeIsNullInDto() {
    BankAccount bankAccount = BankAccount.builder()
            .apiId(UUID.randomUUID().toString())
            .accountType(null)
            .total(new Money(BigDecimal.ZERO))
            .customerDni("12345678A")
            .build();

    BankAccountDTO dto = mapper.toDto(bankAccount);

    assertNotNull(dto);
    assertNull(dto.getAccountType());
  }
}

