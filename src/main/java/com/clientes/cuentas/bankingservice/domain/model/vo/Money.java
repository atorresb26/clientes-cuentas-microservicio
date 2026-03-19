package com.clientes.cuentas.bankingservice.domain.model.vo;

import com.clientes.cuentas.bankingservice.domain.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object representing a monetary amount.
 * Validates on construction that the amount is non-null and greater than or equal to zero.
 */
public record Money(BigDecimal amount) {

  /**
   * Compact constructor that validates the monetary amount on construction.
   *
   * @throws InvalidAmountException if the amount is null or negative
   */
  public Money {
    if (Objects.isNull(amount) || amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new InvalidAmountException("The amount must be greater than or equal to 0");
    }
  }

  /**
   * Factory method to create a {@link Money} value object.
   *
   * @param amount the monetary amount
   * @return a valid {@link Money} instance
   */
  public static Money of(BigDecimal amount) {
    return new Money(amount);
  }
}

