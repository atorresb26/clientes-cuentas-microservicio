package com.clientes.cuentas.demo.infrastructure.api.mapper;

import com.clientes.cuentas.demo.domain.enums.AccountType;
import com.clientes.cuentas.demo.domain.model.BankAccount;
import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.infrastructure.input.dto.BankAccountNoCustomerDTO;
import com.clientes.cuentas.demo.infrastructure.input.dto.CustomerAccountDTO;
import com.clientes.cuentas.demo.infrastructure.input.dto.CustomerDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerApiMapperTest {

  private final CustomerApiMapper mapper = Mappers.getMapper(CustomerApiMapper.class);

  @Test
  void shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.toCustomerAccountDto(null));
    assertNull(mapper.toBankAccountNoCustomerDto(null));
    assertNull(mapper.toCustomerAccountDtoList(null));
    assertNull(mapper.toCustomerDtoList(null));
    assertTrue(mapper.toCustomerDtoList(List.of()).isEmpty());
    assertNull(mapper.toCustomerDto(null));
  }

  @Test
  void shouldMapCustomerToCustomerAccountDTO() {
    BankAccount account = new BankAccount();
    account.setApiId(UUID.randomUUID().toString());
    account.setAccountType(AccountType.NORMAL);
    account.setTotal(100.0);

    Customer customer = new Customer();
    customer.setDni("dni");
    customer.setName("name");
    customer.setSurname1("surname1");
    customer.setSurname2("surname2");
    customer.setBankAccounts(List.of(account));

    CustomerAccountDTO dto = mapper.toCustomerAccountDto(customer);

    assertEquals("dni", dto.getDni());
    assertEquals("name", dto.getName());
    assertEquals("surname1", dto.getSurname1());
    assertEquals("surname2", dto.getSurname2());
    assertEquals(1, dto.getAccounts().size());
    assertEquals(account.getApiId(), dto.getAccounts().getFirst().getApiId().toString());
    assertEquals(AccountType.NORMAL.getName(), dto.getAccounts().getFirst().getAccountType());
  }

  @Test
  void shouldMapBankAccountToDto() {
    BankAccount account = new BankAccount();
    account.setAccountType(AccountType.NORMAL);
    account.setTotal(200.0);

    BankAccountNoCustomerDTO dto = mapper.toBankAccountNoCustomerDto(account);

    assertEquals(AccountType.NORMAL.getName(), dto.getAccountType());
    assertEquals(200.0, dto.getTotal());
  }

  @Test
  void shouldMapCustomerList() {
    Customer customer = new Customer();
    customer.setDni("11111111A");

    List<CustomerAccountDTO> result = mapper.toCustomerAccountDtoList(List.of(customer));

    assertEquals(1, result.size());
    assertEquals("11111111A", result.getFirst().getDni());
  }

  @Test
  void shouldMapCustomerListToCustomerDTOList() {
    Customer customer1 = new Customer();
    customer1.setDni("11111111A");
    customer1.setName("Juan");

    Customer customer2 = new Customer();
    customer2.setDni("22222222B");
    customer2.setName("Maria");

    List<CustomerDTO> result =
            mapper.toCustomerDtoList(List.of(customer1, customer2));

    assertEquals(2, result.size());
    assertEquals("11111111A", result.getFirst().getDni());
    assertEquals("Juan", result.getFirst().getName());
    assertEquals("22222222B", result.get(1).getDni());
    assertEquals("Maria", result.get(1).getName());
  }

  @Test
  void shouldMapCustomerToCustomerDTO() {
    Customer customer = new Customer();
    customer.setDni("11111111A");
    customer.setName("Juan");
    customer.setSurname1("Pérez");
    customer.setSurname2("López");

    CustomerDTO dto = mapper.toCustomerDto(customer);

    assertTrue(Objects.nonNull(dto));
    assertEquals("11111111A", dto.getDni());
    assertEquals("Juan", dto.getName());
    assertEquals("Pérez", dto.getSurname1());
    assertEquals("López", dto.getSurname2());
  }
}
