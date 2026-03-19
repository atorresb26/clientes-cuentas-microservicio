package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.model.vo.Dni;
import com.clientes.cuentas.bankingservice.domain.port.output.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCustomersServiceTest {

  @InjectMocks
  private GetCustomersService service;

  @Mock
  private CustomerRepository customerRepository;

  @Test
  void shouldReturnCustomersWithAccountsFromRepository() {
    List<Customer> expectedCustomers = List.of(
            Customer.builder().id(1L).dni(Dni.of("11111111A")).build(),
            Customer.builder().id(2L).dni(Dni.of("22222222B")).build()
    );

    when(customerRepository.getCustomersAndAccounts()).thenReturn(expectedCustomers);

    List<Customer> result = service.execute();

    assertEquals(expectedCustomers, result);
    verify(customerRepository).getCustomersAndAccounts();
    verifyNoMoreInteractions(customerRepository);
  }

  @Test
  void shouldReturnEmptyListWhenNoCustomersExist() {
    when(customerRepository.getCustomersAndAccounts()).thenReturn(List.of());

    List<Customer> result = service.execute();

    assertTrue(result.isEmpty());
    verify(customerRepository).getCustomersAndAccounts();
    verifyNoMoreInteractions(customerRepository);
  }
}

