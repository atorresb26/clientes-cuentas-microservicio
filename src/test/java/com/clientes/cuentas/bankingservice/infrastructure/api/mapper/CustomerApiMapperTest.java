package com.clientes.cuentas.bankingservice.infrastructure.api.mapper;

import com.clientes.cuentas.bankingservice.domain.enums.AccountType;
import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.model.vo.Dni;
import com.clientes.cuentas.bankingservice.domain.model.vo.Money;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.BankAccountNoCustomerDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.CustomerAccountDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.CustomerDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.PaginatedCustomerAccountDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.PaginatedCustomerDTO;
import com.clientes.cuentas.bankingservice.application.port.model.PageResult;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
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
    assertNull(mapper.toPaginatedCustomerDto(null));
    assertNull(mapper.toPaginatedCustomerAccountDto(null));
  }

  @Test
  void shouldMapCustomerToCustomerAccountDTO() {
    BankAccount account = BankAccount.builder()
            .apiId(UUID.randomUUID().toString())
            .accountType(AccountType.NORMAL)
            .total(new Money(new BigDecimal("100.0")))
            .build();

    Customer customer = Customer.builder()
            .dni(Dni.of("11111111A"))
            .name("name")
            .surname1("surname1")
            .surname2("surname2")
            .bankAccounts(List.of(account))
            .build();

    CustomerAccountDTO dto = mapper.toCustomerAccountDto(customer);

    assertEquals("11111111A", dto.getDni());
    assertEquals("name", dto.getName());
    assertEquals("surname1", dto.getSurname1());
    assertEquals("surname2", dto.getSurname2());
    assertEquals(1, dto.getAccounts().size());
    assertEquals(account.getApiId(), dto.getAccounts().getFirst().getApiId().toString());
    assertEquals(AccountType.NORMAL.getName(), dto.getAccounts().getFirst().getAccountType());
  }

  @Test
  void shouldMapBankAccountToDto() {
    BankAccount account = BankAccount.builder()
            .accountType(AccountType.NORMAL)
            .total(new Money(new BigDecimal("200.00")))
            .build();

    BankAccountNoCustomerDTO dto = mapper.toBankAccountNoCustomerDto(account);

    assertEquals(AccountType.NORMAL.getName(), dto.getAccountType());
    assertEquals(new BigDecimal("200.00"), dto.getTotal());
  }

  @Test
  void shouldMapCustomerList() {
    Customer customer = Customer.builder()
            .dni(Dni.of("11111111A"))
            .build();

    List<CustomerAccountDTO> result = mapper.toCustomerAccountDtoList(List.of(customer));

    assertEquals(1, result.size());
    assertEquals("11111111A", result.getFirst().getDni());
  }

  @Test
  void shouldMapCustomerListToCustomerDTOList() {
    Customer customer1 = Customer.builder()
            .dni(Dni.of("11111111A"))
            .name("Juan")
            .build();

    Customer customer2 = Customer.builder()
            .dni(Dni.of("22222222B"))
            .name("Maria")
            .build();

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
    Customer customer = Customer.builder()
            .dni(Dni.of("11111111A"))
            .name("Juan")
            .surname1("Prez")
            .surname2("Lpez")
            .build();

    CustomerDTO dto = mapper.toCustomerDto(customer);

    assertTrue(Objects.nonNull(dto));
    assertEquals("11111111A", dto.getDni());
    assertEquals("Juan", dto.getName());
    assertEquals("Prez", dto.getSurname1());
    assertEquals("Lpez", dto.getSurname2());
  }

  @Test
  void shouldMapPageResultToPaginatedCustomerDto() {
    Customer customer = Customer.builder()
        .dni(Dni.of("11111111A"))
        .name("Juan")
        .build();
    PageResult<Customer> pageResult = PageResult.<Customer>builder()
        .content(List.of(customer))
        .pageNumber(2)
        .pageSize(10)
        .totalElements(25)
        .totalPages(3)
        .isFirst(false)
        .isLast(true)
        .build();

    PaginatedCustomerDTO dto = mapper.toPaginatedCustomerDto(pageResult);

    assertEquals(1, dto.getContent().size());
    assertEquals("11111111A", dto.getContent().getFirst().getDni());
    assertEquals(2, dto.getCurrentPage());
    assertEquals(10, dto.getPageSize());
    assertEquals(25L, dto.getTotalElements());
    assertEquals(3, dto.getTotalPages());
    assertEquals(false, dto.getIsFirst());
    assertEquals(true, dto.getIsLast());
    assertEquals(false, dto.getHasNext());
    assertEquals(true, dto.getHasPrevious());
  }

  @Test
  void shouldMapPageResultToPaginatedCustomerAccountDto() {
    Customer customer = Customer.builder()
        .dni(Dni.of("22222222B"))
        .name("Maria")
        .build();
    PageResult<Customer> pageResult = PageResult.<Customer>builder()
        .content(List.of(customer))
        .pageNumber(0)
        .pageSize(20)
        .totalElements(1)
        .totalPages(1)
        .isFirst(true)
        .isLast(true)
        .build();

    PaginatedCustomerAccountDTO dto = mapper.toPaginatedCustomerAccountDto(pageResult);

    assertEquals(1, dto.getContent().size());
    assertEquals("22222222B", dto.getContent().getFirst().getDni());
    assertEquals(0, dto.getCurrentPage());
    assertEquals(20, dto.getPageSize());
    assertEquals(1L, dto.getTotalElements());
    assertEquals(1, dto.getTotalPages());
    assertEquals(true, dto.getIsFirst());
    assertEquals(true, dto.getIsLast());
    assertEquals(false, dto.getHasNext());
    assertEquals(false, dto.getHasPrevious());
  }
}
