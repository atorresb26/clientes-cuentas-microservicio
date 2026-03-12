package com.clientes.cuentas.demo.domain.model;

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

  private String accountType;
  private Double total;
}
