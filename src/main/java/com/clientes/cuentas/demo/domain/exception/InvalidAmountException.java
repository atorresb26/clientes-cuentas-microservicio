package com.clientes.cuentas.demo.domain.exception;

public class InvalidAmountException extends RuntimeException {

  public InvalidAmountException(String message) {
    super(message);
  }
}
