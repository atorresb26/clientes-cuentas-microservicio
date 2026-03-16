package com.clientes.cuentas.bankingservice.domain.exception;

/**
 * Exception thrown when the customer DNI does not satisfy the expected format.
 */
public class InvalidCustomerDniException extends RuntimeException {

  public InvalidCustomerDniException(String message) {
    super(message);
  }
}

