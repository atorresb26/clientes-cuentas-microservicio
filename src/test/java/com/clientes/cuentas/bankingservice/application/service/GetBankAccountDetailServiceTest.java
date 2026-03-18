package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.domain.exception.BankAccountNotFoundException;
import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.port.output.BankAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.InvalidParameterException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBankAccountDetailServiceTest {

  @InjectMocks
  private GetBankAccountDetailService service;

  @Mock
  private BankAccountRepository bankAccountRepository;

  @Test
  void shouldReturnBankAccountWhenFoundByApiId() {
    UUID apiId = UUID.randomUUID();
    BankAccount expected = BankAccount.builder().apiId(apiId.toString()).build();

    when(bankAccountRepository.findByApiId(apiId)).thenReturn(Optional.of(expected));

    BankAccount result = service.execute(apiId);

    assertSame(expected, result);
    verify(bankAccountRepository).findByApiId(apiId);
    verifyNoMoreInteractions(bankAccountRepository);
  }

  @Test
  void shouldThrowInvalidParameterExceptionWhenApiIdIsNull() {
    assertThrows(InvalidParameterException.class, () -> service.execute(null));

    verifyNoInteractions(bankAccountRepository);
  }

  @Test
  void shouldThrowBankAccountNotFoundExceptionWhenApiIdNotFound() {
    UUID apiId = UUID.randomUUID();

    when(bankAccountRepository.findByApiId(apiId)).thenReturn(Optional.empty());

    assertThrows(BankAccountNotFoundException.class, () -> service.execute(apiId));

    verify(bankAccountRepository).findByApiId(apiId);
  }

  @Test
  void shouldIncludeApiIdInNotFoundExceptionMessage() {
    UUID apiId = UUID.randomUUID();

    when(bankAccountRepository.findByApiId(apiId)).thenReturn(Optional.empty());

    BankAccountNotFoundException ex = assertThrows(
            BankAccountNotFoundException.class, () -> service.execute(apiId));

    assertTrue(ex.getMessage().contains(apiId.toString()));
  }
}

