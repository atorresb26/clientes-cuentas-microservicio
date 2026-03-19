package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.application.usecase.UpdateBankAccountTotalUseCase;
import com.clientes.cuentas.bankingservice.domain.exception.BankAccountNotFoundException;
import com.clientes.cuentas.bankingservice.domain.port.output.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Implements the use case for updating the total amount of a bank account.
 */
@Service
@RequiredArgsConstructor
public class UpdateBankAccountTotalService implements UpdateBankAccountTotalUseCase {

  private static final String NOT_FOUND = "Bank account not found for apiId %s";

  private final BankAccountRepository bankAccountRepository;

  @Override
  public void execute(UUID apiId, BigDecimal newTotal) {
    validateParameters(apiId, newTotal);
    var account = bankAccountRepository.findByApiId(apiId)
                    .orElseThrow(() -> new BankAccountNotFoundException(String.format(NOT_FOUND, apiId)));
    account.updateTotal(newTotal);
    bankAccountRepository.update(account);
  }

  private static void validateParameters(UUID apiId, BigDecimal newTotal) {
    if (Objects.isNull(apiId)) {
      throw new IllegalArgumentException("apiId must not be null");
    }
    if (Objects.isNull(newTotal) ||  newTotal.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("newTotal must not be null and must be greater than or equal to zero");
    }
  }
}
