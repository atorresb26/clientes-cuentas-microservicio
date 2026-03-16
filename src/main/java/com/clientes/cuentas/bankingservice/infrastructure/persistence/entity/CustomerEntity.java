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
import lombok.ToString;

import java.time.LocalDate;

/**
 * JPA Entity class to map the data from the {@code cliente} table.
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@ToString
@Table(name = "cliente")
public class CustomerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "dni", unique = true, nullable = false)
  private String dni;

  @Column(name = "nombre")
  private String name;

  @Column(name = "apellido1")
  private String surname1;

  @Column(name = "apellido2")
  private String surname2;

  @Column(name = "fecha_nacimiento")
  private LocalDate birthDate;
}
