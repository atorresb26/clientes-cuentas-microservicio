package com.clientes.cuentas.bankingservice.domain.exception;

/**
 * Exception thrown when an invalid amount is provided in a business operation.
 *
 * <p>
 * This exception indicates that the amount supplied does not satisfy
 * the required business constraints (for example, negative values or amounts
 * outside the allowed range).
 * </p>
 */
public class InvalidAmountException extends RuntimeException {

  /**
   * Creates a new {@code InvalidAmountException} with the specified detail message.
   *
   * @param message the detail message describing why the amount is invalid
   */
  public InvalidAmountException(String message) {
    super(message);
  }
}
