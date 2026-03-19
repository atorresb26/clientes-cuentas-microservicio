package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.application.command.CreateBankAccountForCustomerCommand;
import com.clientes.cuentas.bankingservice.application.mapper.BankAccountMapper;
import com.clientes.cuentas.bankingservice.application.repository.BankAccountRepository;
import com.clientes.cuentas.bankingservice.application.repository.CustomerRepository;
import com.clientes.cuentas.bankingservice.domain.enums.AccountType;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidAccountTypeCodeException;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidAmountException;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidCustomerDniException;
import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.model.vo.Dni;
import com.clientes.cuentas.bankingservice.domain.model.vo.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateBankAccountForCustomerServiceTest {

  @InjectMocks
  private CreateBankAccountForCustomerService service;

  @Mock
  private BankAccountRepository bankAccountRepository;

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private BankAccountMapper bankAccountMapper;

  @Test
  void shouldCreateBankAccountForExistingCustomer() {
    CreateBankAccountForCustomerCommand command = new CreateBankAccountForCustomerCommand(
            "12345678A",
            "NRML",
            new BigDecimal("100.00")
    );

    Customer existingCustomer = Customer.builder()
            .id(10L)
            .dni(Dni.of("12345678A"))
            .build();

    BankAccount mappedBankAccount = BankAccount.builder()
            .total(new Money(new BigDecimal("100.00")))
            .build();

    BankAccount persistedBankAccount = BankAccount.builder()
            .apiId("api-1")
            .customerId(10L)
            .total(new Money(new BigDecimal("100.00")))
            .build();

    when(customerRepository.findByDni("12345678A")).thenReturn(Optional.of(existingCustomer));
    when(bankAccountMapper.toBankAccount(command)).thenReturn(mappedBankAccount);
    when(bankAccountRepository.save(mappedBankAccount)).thenReturn(persistedBankAccount);

    BankAccount result = service.execute(command);

    assertSame(persistedBankAccount, result);
    assertEquals(10L, mappedBankAccount.getCustomerId());

    verify(customerRepository).findByDni("12345678A");
    verify(customerRepository, never()).save(org.mockito.ArgumentMatchers.any(Customer.class));
    verify(bankAccountMapper).toBankAccount(command);
    verify(bankAccountRepository).save(mappedBankAccount);
    verifyNoMoreInteractions(customerRepository, bankAccountMapper, bankAccountRepository);
  }

  @Test
  void shouldCreateCustomerWhenItDoesNotExistAndThenCreateBankAccount() {
    CreateBankAccountForCustomerCommand command = new CreateBankAccountForCustomerCommand(
            "99999999Z",
            "PREM",
            new BigDecimal("300.00")
    );

    Customer createdCustomer = Customer.builder()
            .id(77L)
            .dni(Dni.of("99999999Z"))
            .build();

    BankAccount mappedBankAccount = BankAccount.builder()
            .total(new Money(new BigDecimal("300.00")))
            .build();

    BankAccount persistedBankAccount = BankAccount.builder()
            .apiId("api-2")
            .customerId(77L)
            .total(new Money(new BigDecimal("300.00")))
            .build();

    when(customerRepository.findByDni("99999999Z")).thenReturn(Optional.empty());
    when(customerRepository.save(org.mockito.ArgumentMatchers.any(Customer.class))).thenReturn(createdCustomer);
    when(bankAccountMapper.toBankAccount(command)).thenReturn(mappedBankAccount);
    when(bankAccountRepository.save(mappedBankAccount)).thenReturn(persistedBankAccount);

    BankAccount result = service.execute(command);

    assertSame(persistedBankAccount, result);
    assertEquals(77L, mappedBankAccount.getCustomerId());

    ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
    verify(customerRepository).findByDni("99999999Z");
    verify(customerRepository).save(customerCaptor.capture());
    assertEquals("99999999Z", customerCaptor.getValue().getDni().value());

    verify(bankAccountMapper).toBankAccount(command);

    ArgumentCaptor<BankAccount> bankAccountCaptor = ArgumentCaptor.forClass(BankAccount.class);
    verify(bankAccountRepository).save(bankAccountCaptor.capture());
    assertEquals(77L, bankAccountCaptor.getValue().getCustomerId());
    assertEquals(new BigDecimal("300.00"), bankAccountCaptor.getValue().getTotal().amount());

    verifyNoMoreInteractions(customerRepository, bankAccountMapper, bankAccountRepository);
  }

  @Test
  void shouldPropagateExceptionWhenBankAccountSaveFails() {
    CreateBankAccountForCustomerCommand command = new CreateBankAccountForCustomerCommand(
            "00000000T",
            "NRML",
            new BigDecimal("50.00")
    );

    Customer createdCustomer = Customer.builder()
            .id(15L)
            .dni(Dni.of("00000000T"))
            .build();

    BankAccount mappedBankAccount = BankAccount.builder()
            .total(new Money(new BigDecimal("50.00")))
            .build();

    RuntimeException expectedException = new RuntimeException("DB error saving bank account");

    when(customerRepository.findByDni("00000000T")).thenReturn(Optional.empty());
    when(customerRepository.save(org.mockito.ArgumentMatchers.any(Customer.class))).thenReturn(createdCustomer);
    when(bankAccountMapper.toBankAccount(command)).thenReturn(mappedBankAccount);
    when(bankAccountRepository.save(mappedBankAccount)).thenThrow(expectedException);

    RuntimeException ex = assertThrows(RuntimeException.class, () -> service.execute(command));

    assertSame(expectedException, ex);
    assertEquals(15L, mappedBankAccount.getCustomerId());

    verify(customerRepository).findByDni("00000000T");
    verify(customerRepository).save(org.mockito.ArgumentMatchers.any(Customer.class));
    verify(bankAccountMapper).toBankAccount(command);
    verify(bankAccountRepository).save(mappedBankAccount);
    verifyNoMoreInteractions(customerRepository, bankAccountMapper, bankAccountRepository);
  }

  @Test
  void shouldThrowInvalidAmountExceptionWhenTotalIsNull() {
    CreateBankAccountForCustomerCommand command = new CreateBankAccountForCustomerCommand(
            "12345678A",
            "NRML",
            null
    );

    InvalidAmountException ex = assertThrows(InvalidAmountException.class, () -> service.execute(command));

    assertEquals("The amount must be greater than or equal to 0", ex.getMessage());
    verifyNoInteractions(customerRepository, bankAccountMapper, bankAccountRepository);
  }

  @Test
  void shouldThrowInvalidAmountExceptionWhenTotalIsNegative() {
    CreateBankAccountForCustomerCommand command = new CreateBankAccountForCustomerCommand(
            "12345678A",
            "NRML",
            new BigDecimal("-1.00")
    );

    InvalidAmountException ex = assertThrows(InvalidAmountException.class, () -> service.execute(command));

    assertEquals("The amount must be greater than or equal to 0", ex.getMessage());
    verifyNoInteractions(customerRepository, bankAccountMapper, bankAccountRepository);
  }

  @Test
  void shouldThrowInvalidCustomerDniExceptionWhenDniFormatIsInvalid() {
    CreateBankAccountForCustomerCommand command = new CreateBankAccountForCustomerCommand(
            "1234A",
            "NRML",
            new BigDecimal("10.00")
    );

    InvalidCustomerDniException ex = assertThrows(InvalidCustomerDniException.class, () -> service.execute(command));

    assertEquals("The customer DNI format is invalid. It must match 8 digits and 1 letter", ex.getMessage());
    verifyNoInteractions(customerRepository, bankAccountMapper, bankAccountRepository);
  }

  @Test
  void shouldThrowInvalidAccountTypeCodeExceptionWhenAccountTypeCodeIsNull() {
    CreateBankAccountForCustomerCommand command = new CreateBankAccountForCustomerCommand(
            "12345678A",
            null,
            new BigDecimal("10.00")
    );

    InvalidAccountTypeCodeException ex = assertThrows(InvalidAccountTypeCodeException.class, () -> service.execute(command));

    assertEquals(
            String.format("Invalid account type code. Accepted values are: %s", AccountType.getAcceptedCodesMessage()),
            ex.getMessage());
    verifyNoInteractions(customerRepository, bankAccountMapper, bankAccountRepository);
  }

  @Test
  void shouldThrowInvalidAccountTypeCodeExceptionWhenAccountTypeCodeIsInvalid() {
    CreateBankAccountForCustomerCommand command = new CreateBankAccountForCustomerCommand(
            "12345678A",
            "VIP",
            new BigDecimal("10.00")
    );

    InvalidAccountTypeCodeException ex = assertThrows(InvalidAccountTypeCodeException.class, () -> service.execute(command));

    assertEquals(
            String.format("Invalid account type code 'VIP'. Accepted values are: %s", AccountType.getAcceptedCodesMessage()),
            ex.getMessage());
    verifyNoInteractions(customerRepository, bankAccountMapper, bankAccountRepository);
  }
}
