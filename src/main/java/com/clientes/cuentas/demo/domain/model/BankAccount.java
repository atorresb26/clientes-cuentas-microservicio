package com.clientes.cuentas.demo.domain.model;

import com.clientes.cuentas.demo.domain.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * The Bank Account domain model.
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

  private String apiId;
  private AccountType accountType;
  private BigDecimal total;
  private Long customerId;
}
