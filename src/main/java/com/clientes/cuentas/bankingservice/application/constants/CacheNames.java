package com.clientes.cuentas.bankingservice.application.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Centralizes cache name constants to avoid magic strings across the application.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CacheNames {

  /** Cache for {@code getCustomerByDni}: keyed by DNI. Evicted on create/update account. */
  public static final String CUSTOMER_BY_DNI = "customer-with-accounts";

  /** Cache for {@code getBankAccountByApiId}: keyed by UUID. Evicted on update total. */
  public static final String BANK_ACCOUNTS = "bank-accounts";

  /** Cache for {@code AccountTypeReferenceMapper}: keyed by code. Reference data, rarely changes. */
  public static final String ACCOUNT_TYPES = "account-types";
}

