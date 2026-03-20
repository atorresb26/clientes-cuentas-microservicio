package com.clientes.cuentas.bankingservice.domain.exception;

/**
 * Exception thrown when a bank account cannot be found for the provided identifier.
 */
public class BankAccountNotFoundException extends RuntimeException {

  /**
   * Creates a new exception indicating that no bank account was found.
   *
   * @param message the message describing the reason for the exception
   */
  public BankAccountNotFoundException(String message) {
    super(message);
  }
}
