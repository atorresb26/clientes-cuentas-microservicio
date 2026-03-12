package com.clientes.cuentas.demo.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Customer domain object.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Customer {

  private String dni;
  private String name;
  private String surname1;
  private String surname2;
  private LocalDate birthDate;

  private List<BankAccount> bankAccounts;

  /**
   * Add bank account to the list and check if it is initialized.
   *
   * @param bankAccount the account to add
   */
  public void addBankAccount(BankAccount bankAccount) {
    if (Objects.isNull(bankAccounts)) {
      bankAccounts = new ArrayList<>();
    }
    bankAccounts.add(bankAccount);
  }
}
