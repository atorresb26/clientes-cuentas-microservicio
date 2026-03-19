package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.application.repository.CustomerRepository;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.model.vo.Dni;
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
class GetAdultCustomersServiceTest {

  @InjectMocks
  private GetAdultCustomersService service;

  @Mock
  private CustomerRepository customerRepository;

  @Test
  void shouldReturnAdultCustomerListFromRepository() {
    List<Customer> expectedCustomers = List.of(
            Customer.builder().id(1L).dni(Dni.of("11111111A")).build(),
            Customer.builder().id(2L).dni(Dni.of("22222222B")).build()
    );

    when(customerRepository.getAdultCustomers()).thenReturn(expectedCustomers);

    List<Customer> result = service.execute();

    assertEquals(expectedCustomers, result);
    verify(customerRepository).getAdultCustomers();
    verifyNoMoreInteractions(customerRepository);
  }

  @Test
  void shouldReturnEmptyListWhenNoAdultCustomersExist() {
    when(customerRepository.getAdultCustomers()).thenReturn(List.of());

    List<Customer> result = service.execute();

    assertTrue(result.isEmpty());
    verify(customerRepository).getAdultCustomers();
    verifyNoMoreInteractions(customerRepository);
  }
}

