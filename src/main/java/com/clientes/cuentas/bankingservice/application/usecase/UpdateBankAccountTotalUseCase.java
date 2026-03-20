package com.clientes.cuentas.bankingservice.application.usecase;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Use case for updating the total amount of a bank account.
 */
public interface UpdateBankAccountTotalUseCase {

  /**
   * Updates the total amount of a bank account by its API identifier.
   *
   * @param apiId    the API identifier of the bank account
   * @param newTotal the new total amount to set for the bank account
   */
  void execute(UUID apiId, BigDecimal newTotal);
}
