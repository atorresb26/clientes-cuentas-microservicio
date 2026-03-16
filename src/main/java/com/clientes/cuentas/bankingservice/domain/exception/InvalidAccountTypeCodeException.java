package com.clientes.cuentas.bankingservice.domain.exception;

/**
 * Exception thrown when the account type code is not one of the supported values.
 */
public class InvalidAccountTypeCodeException extends RuntimeException {

  public InvalidAccountTypeCodeException(String message) {
    super(message);
  }
}

