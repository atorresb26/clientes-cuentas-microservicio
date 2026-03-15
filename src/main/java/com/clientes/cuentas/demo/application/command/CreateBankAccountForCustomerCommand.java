package com.clientes.cuentas.demo.application.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
  private Double total;

}
