package com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper;

import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.model.vo.Dni;
import com.clientes.cuentas.bankingservice.domain.model.vo.Money;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.CustomerEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerEntityMapperTest {

  private final CustomerEntityMapper mapper = Mappers.getMapper(CustomerEntityMapper.class);

  @Test
  void shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.toCustomerList(null));
    assertTrue(mapper.toCustomerList(List.of()).isEmpty());
    assertNull(mapper.toCustomer(null));
    assertNull(mapper.toEntity(null));
  }

  @Test
  void shouldMapCustomerEntityListToCustomerListPreservingOrderAndFields() {
    CustomerEntity entity1 = new CustomerEntity();
    entity1.setId(1L);
    entity1.setDni("11111111A");
    entity1.setName("Juan");
    entity1.setSurname1("Perez");
    entity1.setSurname2("Lopez");
    entity1.setBirthDate(LocalDate.of(1990, 1, 10));

    CustomerEntity entity2 = new CustomerEntity();
    entity2.setId(2L);
    entity2.setDni("22222222B");
    entity2.setName("Maria");
    entity2.setSurname1("Garcia");
    entity2.setSurname2("Sanchez");
    entity2.setBirthDate(LocalDate.of(1988, 5, 20));

    List<Customer> result = mapper.toCustomerList(List.of(entity1, entity2));

    assertEquals(2, result.size());
    assertEquals(1L, result.get(0).getId());
    assertEquals(2L, result.get(1).getId());
    assertEquals("11111111A", result.get(0).getDni().value());
    assertEquals("22222222B", result.get(1).getDni().value());
    assertEquals("Juan", result.get(0).getName());
    assertEquals("Maria", result.get(1).getName());
    assertEquals("Perez", result.get(0).getSurname1());
    assertEquals("Sanchez", result.get(1).getSurname2());
    assertEquals(LocalDate.of(1990, 1, 10), result.get(0).getBirthDate());
    assertEquals(LocalDate.of(1988, 5, 20), result.get(1).getBirthDate());
  }

  @Test
  void shouldKeepNullElementsWhenMappingCustomerEntityList() {
    CustomerEntity entity = new CustomerEntity();
    entity.setId(7L);
    entity.setDni("77777777G");
    entity.setName("Ana");

    List<Customer> result = mapper.toCustomerList(Arrays.asList(entity, null));

    assertEquals(2, result.size());
    assertNotNull(result.getFirst());
    assertEquals(7L, result.getFirst().getId());
    assertEquals("77777777G", result.getFirst().getDni().value());
    assertNull(result.get(1));
  }

  @Test
  void shouldMapCustomerEntityToCustomerIgnoringBankAccounts() {
    CustomerEntity entity = new CustomerEntity();
    entity.setId(1L);
    entity.setDni("12345678A");
    entity.setName("Juan");
    entity.setSurname1("Perez");
    entity.setSurname2("Lopez");
    entity.setBirthDate(LocalDate.of(1993, 9, 1));

    Customer customer = mapper.toCustomer(entity);

    assertNotNull(customer);
    assertEquals(1L, customer.getId());
    assertEquals("12345678A", customer.getDni().value());
    assertEquals("Juan", customer.getName());
    assertEquals("Perez", customer.getSurname1());
    assertEquals("Lopez", customer.getSurname2());
    assertEquals(LocalDate.of(1993, 9, 1), customer.getBirthDate());
    assertNotNull(customer.getBankAccounts());
    assertTrue(customer.getBankAccounts().isEmpty());
  }

  @Test
  void shouldMapCustomerToCustomerEntity() {
    BankAccount bankAccount = BankAccount.builder()
            .apiId("api-1")
            .total(new Money(new BigDecimal("250.50")))
            .customerId(10L)
            .build();

    Customer customer = Customer.builder()
            .id(10L)
            .dni(Dni.of("99999999Z"))
            .name("Lucia")
            .surname1("Gomez")
            .surname2("Ruiz")
            .birthDate(LocalDate.of(1985, 12, 31))
            .bankAccounts(List.of(bankAccount))
            .build();

    CustomerEntity result = mapper.toEntity(customer);

    assertNotNull(result);
    assertEquals(10L, result.getId());
    assertEquals("99999999Z", result.getDni());
    assertEquals("Lucia", result.getName());
    assertEquals("Gomez", result.getSurname1());
    assertEquals("Ruiz", result.getSurname2());
    assertEquals(LocalDate.of(1985, 12, 31), result.getBirthDate());
  }
}
