package com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper;

import com.clientes.cuentas.bankingservice.domain.enums.AccountType;
import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.AccountTypeEntity;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.BankAccountEntity;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.CustomerEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountEntityMapperTest {

  // Use the MapStruct-generated implementation so Mockito can inject the sub-mapper mocks.
  @InjectMocks
  private BankAccountEntityMapperImpl mapper;

  @Mock
  private AccountTypeEntityMapper accountTypeEntityMapper;

  @Mock
  private CustomerReferenceMapper customerReferenceMapper;

  @Mock
  private AccountTypeReferenceMapper accountTypeReferenceMapper;

  // -------------------------------------------------------------------------
  // toEntity() — null guard
  // -------------------------------------------------------------------------

  @Test
  void shouldReturnNullWhenBankAccountIsNull() {
    assertNull(mapper.toEntity(null));
    verifyNoInteractions(accountTypeEntityMapper, customerReferenceMapper, accountTypeReferenceMapper);
  }

  // -------------------------------------------------------------------------
  // toEntity() — happy path
  // -------------------------------------------------------------------------

  @Test
  void shouldMapBankAccountToEntityDelegatingToSubMappers() {
    BankAccount bankAccount = BankAccount.builder()
            .id(1L)
            .apiId("api-001")
            .customerId(10L)
            .accountType(AccountType.NORMAL)
            .total(new BigDecimal("500.00"))
            .build();

    CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setId(10L);

    AccountTypeEntity accountTypeEntity = new AccountTypeEntity();
    accountTypeEntity.setCode("NRML");

    when(customerReferenceMapper.mapFromCustomerId(10L)).thenReturn(customerEntity);
    when(accountTypeReferenceMapper.mapFromCode("NRML")).thenReturn(accountTypeEntity);

    BankAccountEntity result = mapper.toEntity(bankAccount);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("api-001", result.getApiId());
    assertEquals(new BigDecimal("500.00"), result.getTotal());
    assertSame(customerEntity, result.getCustomer());
    assertSame(accountTypeEntity, result.getAccountType());

    verify(customerReferenceMapper).mapFromCustomerId(10L);
    verify(accountTypeReferenceMapper).mapFromCode("NRML");
    verifyNoInteractions(accountTypeEntityMapper);
  }

  @Test
  void shouldMapBankAccountToEntityWhenAccountTypeIsNull() {
    BankAccount bankAccount = BankAccount.builder()
            .id(2L)
            .apiId("api-002")
            .customerId(20L)
            .accountType(null)
            .total(new BigDecimal("100.00"))
            .build();

    CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setId(20L);

    when(customerReferenceMapper.mapFromCustomerId(20L)).thenReturn(customerEntity);
    // When accountType is null the generated code extracts null as the code
    when(accountTypeReferenceMapper.mapFromCode(null)).thenReturn(null);

    BankAccountEntity result = mapper.toEntity(bankAccount);

    assertNotNull(result);
    assertNull(result.getAccountType());
    assertSame(customerEntity, result.getCustomer());

    verify(customerReferenceMapper).mapFromCustomerId(20L);
    verify(accountTypeReferenceMapper).mapFromCode(null);
  }

  @Test
  void shouldMapBankAccountToEntityWhenCustomerIdIsNull() {
    BankAccount bankAccount = BankAccount.builder()
            .id(3L)
            .apiId("api-003")
            .customerId(null)
            .accountType(AccountType.JUNIOR)
            .total(new BigDecimal("0.00"))
            .build();

    AccountTypeEntity accountTypeEntity = new AccountTypeEntity();
    accountTypeEntity.setCode("JR");

    when(customerReferenceMapper.mapFromCustomerId(null)).thenReturn(null);
    when(accountTypeReferenceMapper.mapFromCode("JR")).thenReturn(accountTypeEntity);

    BankAccountEntity result = mapper.toEntity(bankAccount);

    assertNotNull(result);
    assertNull(result.getCustomer());
    assertSame(accountTypeEntity, result.getAccountType());
  }

  // -------------------------------------------------------------------------
  // toDomainObject() — null guard
  // -------------------------------------------------------------------------

  @Test
  void shouldReturnNullWhenEntityIsNull() {
    assertNull(mapper.toDomainObject(null));
    verifyNoInteractions(accountTypeEntityMapper, customerReferenceMapper, accountTypeReferenceMapper);
  }

  // -------------------------------------------------------------------------
  // toDomainObject() — happy path
  // -------------------------------------------------------------------------

  @Test
  void shouldMapEntityToDomainObjectDelegatingAccountTypeToSubMapper() {
    CustomerEntity customer = new CustomerEntity();
    customer.setId(5L);
    customer.setDni("12345678A");

    AccountTypeEntity accountTypeEntity = new AccountTypeEntity();
    accountTypeEntity.setCode("PREM");

    BankAccountEntity entity = new BankAccountEntity();
    entity.setId(1L);
    entity.setApiId("api-xyz");
    entity.setTotal(new BigDecimal("1000.00"));
    entity.setCustomer(customer);
    entity.setAccountType(accountTypeEntity);

    when(accountTypeEntityMapper.fromEntity(accountTypeEntity)).thenReturn(AccountType.PREMIUM);

    BankAccount result = mapper.toDomainObject(entity);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("api-xyz", result.getApiId());
    assertEquals(new BigDecimal("1000.00"), result.getTotal());
    assertEquals(5L, result.getCustomerId());
    assertEquals("12345678A", result.getCustomerDni());
    assertEquals(AccountType.PREMIUM, result.getAccountType());

    verify(accountTypeEntityMapper).fromEntity(accountTypeEntity);
    verifyNoInteractions(customerReferenceMapper, accountTypeReferenceMapper);
  }

  @Test
  void shouldMapEntityToDomainObjectWithNullCustomer() {
    BankAccountEntity entity = new BankAccountEntity();
    entity.setId(2L);
    entity.setApiId("api-no-customer");
    entity.setTotal(new BigDecimal("0.00"));
    entity.setCustomer(null);
    entity.setAccountType(null);

    when(accountTypeEntityMapper.fromEntity(null)).thenReturn(null);

    BankAccount result = mapper.toDomainObject(entity);

    assertNotNull(result);
    assertNull(result.getCustomerId());
    assertNull(result.getCustomerDni());
    assertNull(result.getAccountType());

    verify(accountTypeEntityMapper).fromEntity(null);
    verifyNoInteractions(customerReferenceMapper, accountTypeReferenceMapper);
  }

  @Test
  void shouldPreserveAllScalarFieldsWhenMappingEntityToDomainObject() {
    CustomerEntity customer = new CustomerEntity();
    customer.setId(99L);
    customer.setDni("99999999Z");

    AccountTypeEntity accountTypeEntity = new AccountTypeEntity();
    BankAccountEntity entity = new BankAccountEntity();
    entity.setId(77L);
    entity.setApiId("api-scalar-test");
    entity.setTotal(new BigDecimal("250.75"));
    entity.setCustomer(customer);
    entity.setAccountType(accountTypeEntity);

    when(accountTypeEntityMapper.fromEntity(accountTypeEntity)).thenReturn(AccountType.JUNIOR);

    BankAccount result = mapper.toDomainObject(entity);

    assertEquals(77L, result.getId());
    assertEquals("api-scalar-test", result.getApiId());
    assertEquals(new BigDecimal("250.75"), result.getTotal());
    assertEquals(99L, result.getCustomerId());
    assertEquals("99999999Z", result.getCustomerDni());
    assertEquals(AccountType.JUNIOR, result.getAccountType());
  }
}

