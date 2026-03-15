package com.clientes.cuentas.demo.domain.port.output;

import com.clientes.cuentas.demo.domain.model.BankAccount;

/**
 * Output port for the repository dedicated to the {@code cuenta_bancaria} table
 */
public interface BankAccountRepository {

  BankAccount save(BankAccount bankAccount);
}
