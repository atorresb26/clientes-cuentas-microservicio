package com.clientes.cuentas.demo.domain.port.output;

import com.clientes.cuentas.demo.domain.model.BankAccount;

/**
 * Output port for the repository dedicated to the {@code cuenta_bancaria} table
 */
public interface BankAccountRepository {

  /**
   * Persists the given {@link BankAccount} in the underlying persistence store.
   *
   * @param bankAccount domain bank account to be persisted
   * @return the persisted bank account instance
   */
  BankAccount save(BankAccount bankAccount);
}
