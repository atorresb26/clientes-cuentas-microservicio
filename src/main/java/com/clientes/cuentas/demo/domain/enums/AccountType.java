package com.clientes.cuentas.demo.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum AccountType {

  JUNIOR("JR", "JUNIOR"),
  NORMAL("NRML", "NORMAL"),
  PREMIUM("PREM", "PREMIUM");

  private final String code;
  private final String name;

  /**
   * Returns the name of the account type associated with the given code.
   * <p>
   * This method searches through the available {@link AccountType} values
   * and returns the corresponding name for the provided code.
   *
   * @param code the account type code used to identify the account type
   * @return the name associated with the given account type code
   * @throws IllegalArgumentException if no account type exists for the given code
   */
  public static String getNameByCode(String code) {
    return Arrays.stream(AccountType.values())
            .filter(type -> type.getCode().equals(code))
            .map(AccountType::getName)
            .findFirst()
            .orElseThrow(() -> new EnumConstantNotPresentException(AccountType.class, code));
  }

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
}
