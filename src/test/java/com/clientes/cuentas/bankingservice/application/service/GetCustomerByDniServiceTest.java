package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.domain.exception.CustomerNotFoundException;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.model.vo.Dni;
import com.clientes.cuentas.bankingservice.domain.port.output.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCustomerByDniServiceTest {

  @InjectMocks
  private GetCustomerByDniService service;

  @Mock
  private CustomerRepository customerRepository;

  @Test
  void shouldReturnCustomerWithAccountsWhenFoundByDni() {
    String dni = "12345678A";
    Customer expected = Customer.builder().id(1L).dni(Dni.of(dni)).build();

    when(customerRepository.findByDniWithAccounts(dni)).thenReturn(Optional.of(expected));

    Customer result = service.execute(dni);

    assertSame(expected, result);
    verify(customerRepository).findByDniWithAccounts(dni);
    verifyNoMoreInteractions(customerRepository);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenDniIsNull() {
    assertThrows(IllegalArgumentException.class, () -> service.execute(null));

    verifyNoInteractions(customerRepository);
  }

  @Test
  void shouldThrowCustomerNotFoundExceptionWhenDniNotFound() {
    String dni = "99999999Z";

    when(customerRepository.findByDniWithAccounts(dni)).thenReturn(Optional.empty());

    assertThrows(CustomerNotFoundException.class, () -> service.execute(dni));

    verify(customerRepository).findByDniWithAccounts(dni);
  }

  @Test
  void shouldIncludeDniInNotFoundExceptionMessage() {
    String dni = "12345678A";

    when(customerRepository.findByDniWithAccounts(dni)).thenReturn(Optional.empty());

    CustomerNotFoundException ex = assertThrows(
            CustomerNotFoundException.class, () -> service.execute(dni));

    assertTrue(ex.getMessage().contains(dni));
  }
}

