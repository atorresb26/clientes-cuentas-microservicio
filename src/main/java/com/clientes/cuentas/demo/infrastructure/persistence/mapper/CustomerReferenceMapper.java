package com.clientes.cuentas.demo.infrastructure.persistence.mapper;

import com.clientes.cuentas.demo.infrastructure.persistence.entity.CustomerEntity;
import com.clientes.cuentas.demo.infrastructure.persistence.repository.JpaCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomerReferenceMapper {

  private final JpaCustomerRepository jpaCustomerRepository;

  public CustomerEntity mapFromCustomerId(Long id) {
    if (Objects.isNull(id)) {
      return null;
    }
    return jpaCustomerRepository.getReferenceById(id);
  }
}
