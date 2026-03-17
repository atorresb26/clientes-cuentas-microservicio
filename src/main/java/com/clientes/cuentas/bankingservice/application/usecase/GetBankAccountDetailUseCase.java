package com.clientes.cuentas.bankingservice.application.usecase;

import com.clientes.cuentas.bankingservice.domain.model.BankAccount;

import java.util.UUID;

/**
 * Use case contract for retrieving bank account details by API identifier.
 */
public interface GetBankAccountDetailUseCase {

  /**
   * Retrieves the bank account details associated with the provided API identifier.
   *
   * @param apiId the API identifier of the bank account
   * @return the bank account details
   */
  BankAccount execute(UUID apiId);
}
