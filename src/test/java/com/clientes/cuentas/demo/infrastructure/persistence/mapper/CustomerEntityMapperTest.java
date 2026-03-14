package com.clientes.cuentas.demo.infrastructure.persistence.mapper;

import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.infrastructure.persistence.entity.CustomerEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerEntityMapperTest {

  private final CustomerEntityMapper mapper = Mappers.getMapper(CustomerEntityMapper.class);

  @Test
  void shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.toCustomerList(null));
    assertTrue(mapper.toCustomerList(List.of()).isEmpty());
    assertNull(mapper.toCustomer(null));
  }

  @Test
  void shouldMapCustomerEntityListToCustomerList() {
    CustomerEntity entity1 = new CustomerEntity();
    entity1.setId(1L);
    entity1.setName("Juan");

    CustomerEntity entity2 = new CustomerEntity();
    entity2.setId(2L);
    entity2.setName("María");

    List<Customer> result = mapper.toCustomerList(List.of(entity1, entity2));

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getName()).isEqualTo("Juan");
    assertThat(result.get(1).getName()).isEqualTo("María");
  }

  @Test
  void shouldMapCustomerEntityToCustomer() {
    CustomerEntity entity = new CustomerEntity();
    entity.setId(1L);
    entity.setName("Juan");
    entity.setSurname1("Pérez");
    entity.setSurname2("López");

    Customer customer = mapper.toCustomer(entity);

    assertThat(customer).isNotNull();
    assertThat(customer.getName()).isEqualTo("Juan");
    assertThat(customer.getSurname1()).isEqualTo("Pérez");
    assertThat(customer.getSurname2()).isEqualTo("López");

    // campo ignorado
    assertThat(customer.getBankAccounts()).isNull();
  }
}
