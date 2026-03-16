package com.clientes.cuentas.bankingservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA Entity class to map the data from the {@code tipo_cuenta} master table.
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "tipo_cuenta")
public class AccountTypeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "codigo")
  private String code;

  @Column(name = "nombre")
  private String name;
}
