package com.clientes.cuentas.demo.application.service;

import com.clientes.cuentas.demo.domain.exception.InvalidAmountException;
import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.domain.port.output.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCustomersWithHigherAmountServiceTest {

  @InjectMocks
  private GetCustomersWithHigherAmountService service;

  @Mock
  private CustomerRepository customerRepository;

  @Test
  void shouldThrowExceptionWhenAmountIsNull() {
    InvalidAmountException ex = assertThrows(
            InvalidAmountException.class,
            () -> service.execute(null)
    );

    assertEquals("La cantidad debe ser mayor o igual a 0", ex.getMessage());

    verifyNoInteractions(customerRepository);
  }

  @Test
  void shouldThrowExceptionWhenAmountIsNegative() {
    InvalidAmountException ex = assertThrows(
            InvalidAmountException.class,
            () -> service.execute(-10.0)
    );

    assertEquals("La cantidad debe ser mayor o igual a 0", ex.getMessage());

    verifyNoInteractions(customerRepository);
  }

  @Test
  void shouldReturnCustomersWhenAmountIsValid() {
    Double amount = 300.0;

    List<Customer> expectedCustomers = List.of(
            new Customer(),
            new Customer()
    );

    when(customerRepository.getCustomersWithHigherAmount(amount))
            .thenReturn(expectedCustomers);

    List<Customer> result = service.execute(amount);

    assertEquals(expectedCustomers, result);

    verify(customerRepository).getCustomersWithHigherAmount(amount);
    verifyNoMoreInteractions(customerRepository);
  }
}
