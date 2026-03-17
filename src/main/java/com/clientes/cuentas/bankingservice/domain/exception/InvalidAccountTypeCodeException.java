package com.clientes.cuentas.bankingservice.domain.exception;

/**
 * Exception thrown when the account type code is not one of the supported values.
 */
public class InvalidAccountTypeCodeException extends RuntimeException {

  /**
   * Creates a new exception for an invalid account type code.
   *
   * @param message detailed error message.
   */
  public InvalidAccountTypeCodeException(String message) {
    super(message);
  }
}

