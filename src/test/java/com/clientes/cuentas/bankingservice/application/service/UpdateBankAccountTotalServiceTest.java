package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.domain.exception.BankAccountNotFoundException;
import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.model.vo.Money;
import com.clientes.cuentas.bankingservice.domain.port.output.BankAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateBankAccountTotalServiceTest {

  @InjectMocks
  private UpdateBankAccountTotalService service;

  @Mock
  private BankAccountRepository bankAccountRepository;

  // =========================================================================
  // Happy path
  // =========================================================================

  @Test
  void shouldFindAccountSetNewTotalAndPersistIt() {
    UUID apiId = UUID.randomUUID();
    BigDecimal newTotal = new BigDecimal("500.00");
    BankAccount account = BankAccount.builder()
            .apiId(apiId.toString())
            .total(new Money(new BigDecimal("100.00")))
            .build();

    when(bankAccountRepository.findByApiId(apiId)).thenReturn(Optional.of(account));
    when(bankAccountRepository.update(account)).thenReturn(account);

    service.execute(apiId, newTotal);

    // Verify newTotal was applied to the account before persisting
    ArgumentCaptor<BankAccount> captor = ArgumentCaptor.forClass(BankAccount.class);
    verify(bankAccountRepository).update(captor.capture());
    assertEquals(newTotal, captor.getValue().getTotal().amount());

    verify(bankAccountRepository).findByApiId(apiId);
    verifyNoMoreInteractions(bankAccountRepository);
  }

  @Test
  void shouldAcceptZeroAsValidNewTotal() {
    UUID apiId = UUID.randomUUID();
    BankAccount account = BankAccount.builder().apiId(apiId.toString()).build();

    when(bankAccountRepository.findByApiId(apiId)).thenReturn(Optional.of(account));
    when(bankAccountRepository.update(account)).thenReturn(account);

    service.execute(apiId, BigDecimal.ZERO);

    verify(bankAccountRepository).findByApiId(apiId);
    verify(bankAccountRepository).update(account);
  }

  // =========================================================================
  // Validation — apiId
  // =========================================================================

  @Test
  void shouldThrowIllegalArgumentExceptionWhenApiIdIsNull() {
    IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.execute(null, new BigDecimal("100.00")));

    assertEquals("apiId must not be null", ex.getMessage());
    verifyNoInteractions(bankAccountRepository);
  }

  // =========================================================================
  // Validation — newTotal
  // =========================================================================

  @Test
  void shouldThrowIllegalArgumentExceptionWhenNewTotalIsNull() {
    IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.execute(UUID.randomUUID(), null));

    assertEquals("newTotal must not be null and must be greater than or equal to zero",
            ex.getMessage());
    verifyNoInteractions(bankAccountRepository);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenNewTotalIsNegative() {
    IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.execute(UUID.randomUUID(), new BigDecimal("-0.01")));

    assertEquals("newTotal must not be null and must be greater than or equal to zero",
            ex.getMessage());
    verifyNoInteractions(bankAccountRepository);
  }

  // =========================================================================
  // Not found
  // =========================================================================

  @Test
  void shouldThrowBankAccountNotFoundExceptionWhenApiIdNotFound() {
    UUID apiId = UUID.randomUUID();

    when(bankAccountRepository.findByApiId(apiId)).thenReturn(Optional.empty());

    assertThrows(BankAccountNotFoundException.class,
            () -> service.execute(apiId, new BigDecimal("200.00")));

    verify(bankAccountRepository).findByApiId(apiId);
    verifyNoMoreInteractions(bankAccountRepository);
  }

  @Test
  void shouldIncludeApiIdInNotFoundExceptionMessage() {
    UUID apiId = UUID.randomUUID();

    when(bankAccountRepository.findByApiId(apiId)).thenReturn(Optional.empty());

    BankAccountNotFoundException ex = assertThrows(
            BankAccountNotFoundException.class,
            () -> service.execute(apiId, new BigDecimal("100.00")));

    assertEquals("Bank account not found for apiId " + apiId, ex.getMessage());
  }
}

