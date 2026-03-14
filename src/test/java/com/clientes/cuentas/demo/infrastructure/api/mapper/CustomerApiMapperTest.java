package com.clientes.cuentas.demo.infrastructure.api.mapper;

import com.clientes.cuentas.demo.domain.model.BankAccount;
import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.infrastructure.input.dto.BankAccountNoCustomerDTO;
import com.clientes.cuentas.demo.infrastructure.input.dto.CustomerAccountDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CustomerApiMapperTest {

  private final CustomerApiMapper mapper = Mappers.getMapper(CustomerApiMapper.class);

  @Test
  void shouldMapCustomerToCustomerAccountDTO() {
    BankAccount account = new BankAccount();
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
  }

  @Test
  void shouldMapBankAccountToDto() {
    BankAccount account = new BankAccount();
    account.setAccountType("type");
    account.setTotal(200.0);

    BankAccountNoCustomerDTO dto = mapper.toBankAccountNoCustomerDto(account);

    assertEquals("type", dto.getAccountType());
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
  void shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.toCustomerAccountDto(null));
    assertNull(mapper.toBankAccountNoCustomerDto(null));
    assertNull(mapper.toCustomerAccountDtoList(null));
  }
}
