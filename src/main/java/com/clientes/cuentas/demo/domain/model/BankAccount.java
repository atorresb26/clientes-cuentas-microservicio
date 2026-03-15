package com.clientes.cuentas.demo.domain.model;

import com.clientes.cuentas.demo.domain.enums.AccountType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The Bank Account domain model.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BankAccount {

  private String apiId;
  private AccountType accountType;
  private Double total;
  private Long customerId;
}
