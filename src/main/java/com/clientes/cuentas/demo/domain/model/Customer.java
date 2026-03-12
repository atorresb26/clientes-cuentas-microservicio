package com.clientes.cuentas.demo.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Customer {

  private String dni;
  private String name;
  private String surname1;
  private String surname2;
  private LocalDate birthDate;

  private List<BankAccount> bankAccounts;
}
