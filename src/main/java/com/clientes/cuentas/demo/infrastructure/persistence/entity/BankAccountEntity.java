package com.clientes.cuentas.demo.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA Entity class to map the data from the {@code cuenta_bancaria} table.
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "cuenta_bancaria")
public class BankAccountEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "api_id", nullable = false, unique = true)
  private String apiId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cliente_id", nullable = false)
  private CustomerEntity customer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tipo_cuenta_id", nullable = false)
  private AccountTypeEntity accountType;

  @Column(name = "total", nullable = false)
  private Double total;
}
