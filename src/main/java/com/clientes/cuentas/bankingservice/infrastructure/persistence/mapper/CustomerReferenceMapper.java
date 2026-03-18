package com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper;

import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.CustomerEntity;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.repository.JpaCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Mapper component responsible for resolving {@link CustomerEntity} references
 * from a given customer ID using the JPA repository.
 */
@Component
@RequiredArgsConstructor
public class CustomerReferenceMapper {

  private final JpaCustomerRepository jpaCustomerRepository;

  /**
   * Resolves a {@link CustomerEntity} reference from the given customer ID.
   *
   * @param id the unique identifier of the customer; may be {@code null}
   * @return the {@link CustomerEntity} reference associated with the given ID,
   *         or {@code null} if the provided ID is {@code null}
   */
  public CustomerEntity mapFromCustomerId(Long id) {
    if (Objects.isNull(id)) {
      return null;
    }
    return jpaCustomerRepository.getReferenceById(id);
  }
}
