package com.clientes.cuentas.bankingservice.domain.port.output;

import com.clientes.cuentas.bankingservice.domain.model.BankAccount;

import java.util.Optional;
import java.util.UUID;

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

  /**
   * Updates an existing {@link BankAccount} in the underlying persistence store.
   *
   * @param bankAccount domain bank account with updated values to be persisted
   * @return the updated bank account instance
   */
  BankAccount update(BankAccount bankAccount);

  /**
   * Retrieves a bank account by its external API identifier.
   *
   * @param apiId external API identifier of the bank account
   * @return an {@link Optional} containing the matching bank account, or empty if not found
   */
  Optional<BankAccount> findByApiId(UUID apiId);
}
