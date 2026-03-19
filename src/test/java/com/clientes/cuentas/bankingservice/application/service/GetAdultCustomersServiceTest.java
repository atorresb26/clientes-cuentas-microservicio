package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.application.repository.CustomerRepository;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.model.vo.Dni;
import com.clientes.cuentas.bankingservice.application.port.dto.PaginationRequestDTO;
import com.clientes.cuentas.bankingservice.application.port.model.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
  void shouldReturnPaginatedAdultCustomers() {
    List<Customer> expectedCustomers = List.of(
            Customer.builder().id(1L).dni(Dni.of("11111111A")).build(),
            Customer.builder().id(2L).dni(Dni.of("22222222B")).build()
    );
    Page<Customer> page = new PageImpl<>(expectedCustomers, PageRequest.of(0, 20), 2);

    when(customerRepository.getAdultCustomersPaginated(any())).thenReturn(page);

    PaginationRequestDTO pagination = PaginationRequestDTO.builder().page(0).size(20).build();
    PageResult<Customer> result = service.execute(pagination);

    assertEquals(expectedCustomers, result.getContent());
    assertEquals(0, result.getPageNumber());
    assertEquals(20, result.getPageSize());
    assertEquals(2, result.getTotalElements());
    verify(customerRepository).getAdultCustomersPaginated(any());
    verifyNoMoreInteractions(customerRepository);
  }

  @Test
  void shouldReturnEmptyPageWhenNoAdultCustomersExist() {
    Page<Customer> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

    when(customerRepository.getAdultCustomersPaginated(any())).thenReturn(emptyPage);

    PaginationRequestDTO pagination = PaginationRequestDTO.builder().page(0).size(20).build();
    PageResult<Customer> result = service.execute(pagination);

    assertTrue(result.getContent().isEmpty());
    assertEquals(0, result.getTotalElements());
    verify(customerRepository).getAdultCustomersPaginated(any());
    verifyNoMoreInteractions(customerRepository);
  }
}

