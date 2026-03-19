package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.application.constants.CacheNames;
import com.clientes.cuentas.bankingservice.application.repository.BankAccountRepository;
import com.clientes.cuentas.bankingservice.application.usecase.GetBankAccountDetailUseCase;
import com.clientes.cuentas.bankingservice.domain.exception.BankAccountNotFoundException;
import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.UUID;

/**
 * Service implementation of the use case for retrieving bank account details by API identifier.
 */
@Service
@RequiredArgsConstructor
public class GetBankAccountDetailService implements GetBankAccountDetailUseCase {

  private static final String NOT_FOUND = "Account type not found for apiId %s";

  private final BankAccountRepository bankAccountRepository;

  @Override
  @Cacheable(value = CacheNames.BANK_ACCOUNTS, key = "#apiId")
  public BankAccount execute(UUID apiId) {
    if (Objects.isNull(apiId)) {
      throw new InvalidParameterException("apiId must not be null");
    }

    return bankAccountRepository.findByApiId(apiId)
            .orElseThrow(() -> new BankAccountNotFoundException(String.format(NOT_FOUND, apiId)));
  }
}
