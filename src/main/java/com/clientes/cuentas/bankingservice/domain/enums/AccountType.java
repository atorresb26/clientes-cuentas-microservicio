package com.clientes.cuentas.bankingservice.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents the supported bank account types.
 * <p>
 * This enum defines the available account categories and stores
 * the code and display name for each one.
 */
@RequiredArgsConstructor
@Getter
public enum AccountType {

  JUNIOR("JR", "JUNIOR"),
  NORMAL("NRML", "NORMAL"),
  PREMIUM("PREM", "PREMIUM");

  private final String code;
  private final String name;

  /**
   * Returns the AccountType associated with the given code.
   *
   * @param code the account type code
   * @return the corresponding AccountType
   * @throws IllegalArgumentException if no AccountType exists for the given code
   */
  public static AccountType getByCode(String code) {
    return Arrays.stream(AccountType.values())
            .filter(type -> type.getCode().equals(code))
            .findFirst()
            .orElseThrow(() -> new EnumConstantNotPresentException(AccountType.class, code));
  }

  /**
   * Validates if the provided code is one of the supported account type codes.
   *
   * @param code the account type code to validate
   * @return true when the code exists in the enum, false otherwise
   */
  public static boolean isValidCode(String code) {
    return Arrays.stream(AccountType.values())
            .anyMatch(type -> type.getCode().equals(code));
  }

  /**
   * Returns supported account type codes using the same bracket format as previous messages.
   *
   * @return accepted account type codes, e.g. [JR, NRML, PREM]
   */
  public static String getAcceptedCodesMessage() {
    return Arrays.stream(AccountType.values())
            .map(AccountType::getCode)
            .collect(Collectors.joining(", ", "[", "]"));
  }
}
