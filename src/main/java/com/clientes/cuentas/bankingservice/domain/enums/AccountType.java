package com.clientes.cuentas.bankingservice.domain.enums;

import com.clientes.cuentas.bankingservice.domain.exception.InvalidAccountTypeCodeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
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
   * @throws InvalidAccountTypeCodeException if no AccountType exists for the given code
   */
  public static AccountType getByCode(String code) {
    if (Objects.isNull(code)) {
      throw new InvalidAccountTypeCodeException(
              String.format("Invalid account type code. Accepted values are: %s", getAcceptedCodesMessage())
      );
    }

    return Arrays.stream(AccountType.values())
            .filter(type -> type.getCode().equals(code))
            .findFirst()
            .orElseThrow(() -> new InvalidAccountTypeCodeException(
                    String.format("Invalid account type code '%s'. Accepted values are: %s", code, getAcceptedCodesMessage())
            ));
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
