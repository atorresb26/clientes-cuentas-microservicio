package com.clientes.cuentas.demo.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA Entity class to map the data from the {@code cuenta_bancaria} table.
 */
@Entity
@Table(name = "cuenta_bancaria")
public class BankAccountEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "cliente_id", nullable = false)
  private CustomerEntity customer;

  @ManyToOne
  @JoinColumn(name = "tipo_cuenta_id", nullable = false)
  private AccountTypeEntity accountType;

  @Column(name = "total", nullable = false)
  private Double total;
}
