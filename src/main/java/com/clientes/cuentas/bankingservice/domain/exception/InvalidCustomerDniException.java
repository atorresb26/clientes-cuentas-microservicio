package com.clientes.cuentas.bankingservice.domain.exception;

/**
 * Exception thrown when the customer DNI does not satisfy the expected format.
 */
public class InvalidCustomerDniException extends RuntimeException {

  /**
   * Creates a new exception for an invalid customer DNI format.
   *
   * @param message detail message describing the validation error
   */
  public InvalidCustomerDniException(String message) {
    super(message);
  }
}

