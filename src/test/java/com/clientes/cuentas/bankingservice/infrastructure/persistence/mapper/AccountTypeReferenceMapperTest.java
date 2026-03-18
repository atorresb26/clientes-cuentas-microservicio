package com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper;

import com.clientes.cuentas.bankingservice.domain.exception.AccountTypeNotFoundException;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.AccountTypeEntity;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.repository.JpaAccountTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountTypeReferenceMapperTest {

  @InjectMocks
  private AccountTypeReferenceMapper mapper;

  @Mock
  private JpaAccountTypeRepository jpaAccountTypeRepository;

  // -------------------------------------------------------------------------
  // mapFromCode() — null guard
  // -------------------------------------------------------------------------

  @Test
  void shouldReturnNullWhenCodeIsNull() {
    AccountTypeEntity result = mapper.mapFromCode(null);

    assertNull(result);
    verifyNoInteractions(jpaAccountTypeRepository);
  }

  // -------------------------------------------------------------------------
  // mapFromCode() — happy path
  // -------------------------------------------------------------------------

  @Test
  void shouldReturnAccountTypeEntityWhenCodeIsFound() {
    String code = "NRML";
    AccountTypeEntity entity = new AccountTypeEntity();
    entity.setCode(code);
    entity.setName("NORMAL");

    when(jpaAccountTypeRepository.findByCode(code)).thenReturn(Optional.of(entity));

    AccountTypeEntity result = mapper.mapFromCode(code);

    assertSame(entity, result);
    verify(jpaAccountTypeRepository).findByCode(code);
    verifyNoMoreInteractions(jpaAccountTypeRepository);
  }

  @Test
  void shouldReturnCorrectEntityForEveryValidCode() {
    for (String code : new String[]{"JR", "NRML", "PREM"}) {
      AccountTypeEntity entity = new AccountTypeEntity();
      entity.setCode(code);
      when(jpaAccountTypeRepository.findByCode(code)).thenReturn(Optional.of(entity));

      AccountTypeEntity result = mapper.mapFromCode(code);

      assertSame(entity, result, "Expected entity for code: " + code);
    }
  }

  // -------------------------------------------------------------------------
  // mapFromCode() — not found
  // -------------------------------------------------------------------------

  @Test
  void shouldThrowAccountTypeNotFoundExceptionWhenCodeNotFound() {
    String code = "UNKNOWN";
    when(jpaAccountTypeRepository.findByCode(code)).thenReturn(Optional.empty());

    AccountTypeNotFoundException ex = assertThrows(
            AccountTypeNotFoundException.class,
            () -> mapper.mapFromCode(code)
    );

    assertTrue(ex.getMessage().contains(code));
    verify(jpaAccountTypeRepository).findByCode(code);
  }
}

