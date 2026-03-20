package com.clientes.cuentas.bankingservice.application.usecase;

import com.clientes.cuentas.bankingservice.application.command.CreateBankAccountForCustomerCommand;
import com.clientes.cuentas.bankingservice.domain.model.BankAccount;

/**
 * Use case that defines the operation for creating a bank account
 * for an existing customer.
 */
public interface CreateBankAccountForCustomerUseCase {

  /**
   * Executes the use case for creating a bank account for a customer.
   *
   * @param command command object containing the input data required to
   *                create the bank account
   */
  BankAccount execute(CreateBankAccountForCustomerCommand command);
}
