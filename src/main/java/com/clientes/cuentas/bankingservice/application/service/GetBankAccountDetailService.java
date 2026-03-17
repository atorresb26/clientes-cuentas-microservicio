package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.application.usecase.GetBankAccountDetailUseCase;
import com.clientes.cuentas.bankingservice.domain.exception.BankAccountNotFoundException;
import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.port.output.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service implementation of the use case for retrieving bank account details by API identifier.
 */
@Service
@RequiredArgsConstructor
public class GetBankAccountDetailService implements GetBankAccountDetailUseCase {

  private final BankAccountRepository bankAccountRepository;

  @Override
  public BankAccount execute(UUID apiId) {
    return bankAccountRepository.findByApiId(apiId)
            .orElseThrow(() -> new BankAccountNotFoundException(apiId));
  }
}
