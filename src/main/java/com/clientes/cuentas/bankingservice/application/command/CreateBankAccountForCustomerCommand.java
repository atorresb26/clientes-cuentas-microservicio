package com.clientes.cuentas.bankingservice.application.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Command object used by the use case responsible for creating a bank account
 * for an existing customer.
 */
@NoArgsConstructor
@Getter
@Setter
public class CreateBankAccountForCustomerCommand {

  private String customerDni;
  private String accountTypeCode;
  private BigDecimal total;
}
