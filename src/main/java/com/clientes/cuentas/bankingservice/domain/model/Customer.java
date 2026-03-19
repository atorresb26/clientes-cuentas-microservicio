package com.clientes.cuentas.bankingservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The Customer domain object.
 */
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

  private Long id;
  private String dni;
  private String name;
  private String surname1;
  private String surname2;
  private LocalDate birthDate;

  //Builder.Default to initialize the list when using the builder pattern.
  @Builder.Default
  private List<BankAccount> bankAccounts = new ArrayList<>();


  /**
   * Adds a bank account to this customer.
   *
   * @param bankAccount the bank account to add
   */
  public void addBankAccount(BankAccount bankAccount) {
    bankAccounts.add(bankAccount);
  }
}
