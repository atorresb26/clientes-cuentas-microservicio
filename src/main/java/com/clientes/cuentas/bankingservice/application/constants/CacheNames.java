package com.clientes.cuentas.bankingservice.application.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Centralizes cache name constants to avoid magic strings across the application.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CacheNames {

  public static final String CUSTOMER_WITH_ACCOUNTS = "customer-with-accounts";
  public static final String ADULT_CUSTOMERS = "adult-customers";
  public static final String ACCOUNT_TYPES = "account-types";
}

