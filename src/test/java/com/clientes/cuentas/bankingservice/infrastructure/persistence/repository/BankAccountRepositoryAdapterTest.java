package com.clientes.cuentas.bankingservice.infrastructure.persistence.repository;

import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.model.vo.Money;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.BankAccountEntity;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper.BankAccountEntityMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountRepositoryAdapterTest {

  @InjectMocks
  private BankAccountRepositoryAdapter adapter;

  @Mock
  private JpaBankAccountRepository jpaBankAccountRepository;

  @Mock
  private BankAccountEntityMapper mapper;

  // -------------------------------------------------------------------------
  // save()
  // -------------------------------------------------------------------------

  @Test
  void shouldSaveBankAccountAndAssignRandomUuidAsApiId() {
    BankAccount domainAccount = BankAccount.builder()
            .total(new Money(new BigDecimal("100.00")))
            .build();

    BankAccountEntity mappedEntity = new BankAccountEntity();
    BankAccountEntity savedEntity = new BankAccountEntity();
    BankAccount expectedResult = BankAccount.builder()
            .apiId("generated-api-id")
            .total(new Money(new BigDecimal("100.00")))
            .build();

    when(mapper.toEntity(domainAccount)).thenReturn(mappedEntity);
    when(jpaBankAccountRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(mapper.toDomainObject(savedEntity)).thenReturn(expectedResult);

    BankAccount result = adapter.save(domainAccount);

    assertSame(expectedResult, result);

    // The entity must have a valid UUID assigned before it is persisted
    ArgumentCaptor<BankAccountEntity> entityCaptor = ArgumentCaptor.forClass(BankAccountEntity.class);
    verify(jpaBankAccountRepository).save(entityCaptor.capture());
    String assignedApiId = entityCaptor.getValue().getApiId();
    assertNotNull(assignedApiId);
    assertDoesNotThrow(() -> UUID.fromString(assignedApiId),
            "The assigned apiId must be a valid UUID string");

    verify(mapper).toEntity(domainAccount);
    verify(mapper).toDomainObject(savedEntity);
    verifyNoMoreInteractions(mapper, jpaBankAccountRepository);
  }

  @Test
  void shouldAssignDifferentUuidOnEachSave() {
    BankAccount domainAccount = BankAccount.builder().total(new Money(new BigDecimal("50.00"))).build();

    BankAccountEntity entity1 = new BankAccountEntity();
    BankAccountEntity entity2 = new BankAccountEntity();
    BankAccountEntity saved1 = new BankAccountEntity();
    BankAccountEntity saved2 = new BankAccountEntity();
    BankAccount result1 = BankAccount.builder().apiId("r1").build();
    BankAccount result2 = BankAccount.builder().apiId("r2").build();

    when(mapper.toEntity(domainAccount))
            .thenReturn(entity1)
            .thenReturn(entity2);
    when(jpaBankAccountRepository.save(entity1)).thenReturn(saved1);
    when(jpaBankAccountRepository.save(entity2)).thenReturn(saved2);
    when(mapper.toDomainObject(saved1)).thenReturn(result1);
    when(mapper.toDomainObject(saved2)).thenReturn(result2);

    adapter.save(domainAccount);
    adapter.save(domainAccount);

    // Both UUIDs must be valid and different
    assertNotNull(entity1.getApiId());
    assertNotNull(entity2.getApiId());
    assertTrue(() -> !entity1.getApiId().equals(entity2.getApiId()),
            "Each save call must generate a unique UUID");
  }

  @Test
  void shouldPropagateExceptionWhenJpaSaveFailsDuringSave() {
    BankAccount domainAccount = BankAccount.builder().total(new Money(new BigDecimal("50.00"))).build();
    BankAccountEntity mappedEntity = new BankAccountEntity();

    when(mapper.toEntity(domainAccount)).thenReturn(mappedEntity);
    when(jpaBankAccountRepository.save(mappedEntity)).thenThrow(new RuntimeException("DB error on save"));

    assertThrows(RuntimeException.class, () -> adapter.save(domainAccount));
  }

  // -------------------------------------------------------------------------
  // update()
  // -------------------------------------------------------------------------

  @Test
  void shouldUpdateBankAccountAndReturnMappedDomainObject() {
    BankAccount domainAccount = BankAccount.builder()
            .apiId("api-123")
            .total(new Money(new BigDecimal("200.00")))
            .build();

    BankAccountEntity mappedEntity = new BankAccountEntity();
    BankAccountEntity savedEntity = new BankAccountEntity();
    BankAccount expectedResult = BankAccount.builder()
            .apiId("api-123")
            .total(new Money(new BigDecimal("200.00")))
            .build();

    when(mapper.toEntity(domainAccount)).thenReturn(mappedEntity);
    when(jpaBankAccountRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(mapper.toDomainObject(savedEntity)).thenReturn(expectedResult);

    BankAccount result = adapter.update(domainAccount);

    assertSame(expectedResult, result);
    verify(mapper).toEntity(domainAccount);
    verify(jpaBankAccountRepository).save(mappedEntity);
    verify(mapper).toDomainObject(savedEntity);
    verifyNoMoreInteractions(mapper, jpaBankAccountRepository);
  }

  @Test
  void shouldPropagateExceptionWhenJpaSaveFailsDuringUpdate() {
    BankAccount domainAccount = BankAccount.builder().apiId("api-fail").build();
    BankAccountEntity mappedEntity = new BankAccountEntity();

    when(mapper.toEntity(domainAccount)).thenReturn(mappedEntity);
    when(jpaBankAccountRepository.save(mappedEntity)).thenThrow(new RuntimeException("DB error on update"));

    assertThrows(RuntimeException.class, () -> adapter.update(domainAccount));
  }

  // -------------------------------------------------------------------------
  // findByApiId()
  // -------------------------------------------------------------------------

  @Test
  void shouldReturnMappedBankAccountWhenEntityFoundByApiId() {
    UUID apiId = UUID.randomUUID();
    BankAccountEntity entity = new BankAccountEntity();
    BankAccount expectedResult = BankAccount.builder().apiId(apiId.toString()).build();

    when(jpaBankAccountRepository.findByApiId(apiId.toString())).thenReturn(Optional.of(entity));
    when(mapper.toDomainObject(entity)).thenReturn(expectedResult);

    Optional<BankAccount> result = adapter.findByApiId(apiId);

    assertTrue(result.isPresent());
    assertSame(expectedResult, result.get());
    verify(jpaBankAccountRepository).findByApiId(apiId.toString());
    verify(mapper).toDomainObject(entity);
    verifyNoMoreInteractions(jpaBankAccountRepository, mapper);
  }

  @Test
  void shouldReturnEmptyOptionalWhenBankAccountNotFoundByApiId() {
    UUID apiId = UUID.randomUUID();

    when(jpaBankAccountRepository.findByApiId(apiId.toString())).thenReturn(Optional.empty());

    Optional<BankAccount> result = adapter.findByApiId(apiId);

    assertTrue(result.isEmpty());
    verify(jpaBankAccountRepository).findByApiId(apiId.toString());
    verifyNoInteractions(mapper);
  }
}
