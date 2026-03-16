package com.clientes.cuentas.bankingservice.domain.exception;

public class AccountTypeNotFoundException extends RuntimeException {

  public AccountTypeNotFoundException(String code) {
    super(String.format("Account type not found for code %s", code));
  }
}
