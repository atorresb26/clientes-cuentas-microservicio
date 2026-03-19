package com.clientes.cuentas.bankingservice.domain.model.vo;

import com.clientes.cuentas.bankingservice.domain.exception.InvalidCustomerDniException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object representing a Spanish National Identity Document (DNI).
 * Validates on construction that the value matches 8 digits followed by 1 letter.
 */
public record Dni(String value) {

  private static final Pattern PATTERN = Pattern.compile("^[0-9]{8}[A-Za-z]$");

  /**
   * Compact constructor that validates the DNI format on construction.
   *
   * @throws InvalidCustomerDniException if the value is null or does not match the DNI format
   */
  public Dni {
    if (Objects.isNull(value) || !PATTERN.matcher(value).matches()) {
      throw new InvalidCustomerDniException("The customer DNI format is invalid. It must match 8 digits and 1 letter");
    }
  }

  /**
   * Factory method to create a {@link Dni} value object.
   *
   * @param value the raw DNI string
   * @return a valid {@link Dni} instance
   */
  public static Dni of(String value) {
    return new Dni(value);
  }
}

