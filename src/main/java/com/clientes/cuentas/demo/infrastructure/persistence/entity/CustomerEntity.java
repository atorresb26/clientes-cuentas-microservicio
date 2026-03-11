package com.clientes.cuentas.demo.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * JPA Entity class to map the data from the {@code cliente} table.
 */
@Entity
@Table(name = "cliente")
public class CustomerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "dni", unique = true, nullable = false)
  private String dni;

  @Column(name = "nombre", nullable = false)
  private String name;

  @Column(name = "apellido1", nullable = false)
  private String surname1;

  @Column(name = "apellido2")
  private String surname2;

  @Column(name = "fecha_nacimiento", nullable = false)
  private LocalDate birthDate;
}
