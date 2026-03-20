package com.clientes.cuentas.bankingservice.domain.exception;

/**
 * Exception thrown when a customer cannot be found.
 */
public class CustomerNotFoundException extends RuntimeException {

  /**
   * Creates a new exception indicating that no customer was found.
   *
   * @param message the message describing the reason for the exception
   */
  public CustomerNotFoundException(String message) {
    super(message);
  }
}
