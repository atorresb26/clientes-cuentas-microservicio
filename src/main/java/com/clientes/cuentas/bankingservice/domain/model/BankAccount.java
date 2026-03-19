package com.clientes.cuentas.bankingservice.domain.model;

import com.clientes.cuentas.bankingservice.domain.enums.AccountType;
import com.clientes.cuentas.bankingservice.domain.model.vo.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The Bank Account domain model.
 */
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

  private Long id;
  private String apiId;
  private AccountType accountType;
  private Money total;
  private Long customerId;
  private String customerDni;

  /**
   * Associates this bank account with the given customer.
   *
   * @param customerId the internal identifier of the owning customer
   */
  public void assignToCustomer(Long customerId) {
    this.customerId = customerId;
  }

  /**
   * Updates the current balance of this bank account.
   *
   * @param newTotal the new balance as a {@link Money} value object
   */
  public void updateTotal(Money newTotal) {
    this.total = newTotal;
  }
}
