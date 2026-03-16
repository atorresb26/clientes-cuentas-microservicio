package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.domain.exception.InvalidAmountException;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.port.output.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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

    assertEquals("The amount must be greater than or equal to 0", ex.getMessage());

    verifyNoInteractions(customerRepository);
  }

  @Test
  void shouldThrowExceptionWhenAmountIsNegative() {
    BigDecimal amount = new BigDecimal("-10.0");
    InvalidAmountException ex = assertThrows(
            InvalidAmountException.class,
            () -> service.execute(amount)
    );

    assertEquals("The amount must be greater than or equal to 0", ex.getMessage());

    verifyNoInteractions(customerRepository);
  }

  @Test
  void shouldReturnCustomersWhenAmountIsValid() {
    BigDecimal amount = new BigDecimal("300.0");

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
