package com.clientes.cuentas.bankingservice.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a bank account cannot be found for the provided identifier.
 */
public class BankAccountNotFoundException extends RuntimeException {

  /**
   * Creates a new exception indicating that no bank account was found for the given API identifier.
   *
   * @param apiId the API identifier used to search for the bank account
   */
  public BankAccountNotFoundException(UUID apiId) {
    super(String.format("Account type not found for apiId %s", apiId));
  }
}
