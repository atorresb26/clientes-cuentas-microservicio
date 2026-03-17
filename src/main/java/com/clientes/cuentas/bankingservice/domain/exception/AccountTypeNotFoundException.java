package com.clientes.cuentas.bankingservice.domain.exception;

/**
 * Exception thrown when an account type cannot be found for a given code.
 */
public class AccountTypeNotFoundException extends RuntimeException {

  /**
   * Creates the exception when no account type matches the provided code.
   *
   * @param code account type code that was not found
   */
  public AccountTypeNotFoundException(String code) {
    super(String.format("Account type not found for code %s", code));
  }
}
