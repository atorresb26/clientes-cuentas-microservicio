package com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper;

import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.CustomerEntity;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.repository.JpaCustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerReferenceMapperTest {

  @InjectMocks
  private CustomerReferenceMapper mapper;

  @Mock
  private JpaCustomerRepository jpaCustomerRepository;


  @Test
  void shouldReturnNullWhenIdIsNull() {
    CustomerEntity result = mapper.mapFromCustomerId(null);

    assertNull(result);
    verifyNoInteractions(jpaCustomerRepository);
  }

  @Test
  void shouldReturnEntityReferenceWhenIdIsNotNull() {
    Long id = 42L;
    CustomerEntity entity = new CustomerEntity();
    entity.setId(id);
    entity.setDni("12345678A");

    when(jpaCustomerRepository.getReferenceById(id)).thenReturn(entity);

    CustomerEntity result = mapper.mapFromCustomerId(id);

    assertSame(entity, result);
    verify(jpaCustomerRepository).getReferenceById(id);
    verifyNoMoreInteractions(jpaCustomerRepository);
  }

  @Test
  void shouldPropagateExceptionWhenJpaGetReferenceByIdFails() {
    Long id = 99L;
    when(jpaCustomerRepository.getReferenceById(id))
            .thenThrow(new jakarta.persistence.EntityNotFoundException("Customer not found: " + id));

    assertThrows(jakarta.persistence.EntityNotFoundException.class,
            () -> mapper.mapFromCustomerId(id));
  }
}
