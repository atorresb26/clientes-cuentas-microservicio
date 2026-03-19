package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.application.pagination.PageResult;
import com.clientes.cuentas.bankingservice.application.pagination.PaginationCriteria;
import com.clientes.cuentas.bankingservice.application.repository.CustomerRepository;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    PaginationCriteria pagination = PaginationCriteria.builder().page(0).size(20).build();
    IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.execute(null, pagination)
    );

    assertEquals("The amount must be greater than or equal to 0", ex.getMessage());

    verifyNoInteractions(customerRepository);
  }

  @Test
  void shouldThrowExceptionWhenAmountIsNegative() {
    BigDecimal amount = new BigDecimal("-10.0");
    PaginationCriteria pagination = PaginationCriteria.builder().page(0).size(20).build();
    IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.execute(amount, pagination)
    );

    assertEquals("The amount must be greater than or equal to 0", ex.getMessage());

    verifyNoInteractions(customerRepository);
  }

  @Test
  void shouldReturnPaginatedCustomersWhenAmountIsValid() {
    BigDecimal amount = new BigDecimal("300.0");

    List<Customer> expectedCustomers = List.of(
            new Customer(),
            new Customer()
    );
    Page<Customer> page = new PageImpl<>(expectedCustomers, PageRequest.of(0, 20), 2);

    when(customerRepository.getCustomersWithHigherAmountPaginated(eq(amount), any()))
            .thenReturn(page);

    PaginationCriteria pagination = PaginationCriteria.builder().page(0).size(20).build();
    PageResult<Customer> result = service.execute(amount, pagination);

    assertEquals(expectedCustomers, result.getContent());
    assertEquals(2, result.getTotalElements());

    verify(customerRepository).getCustomersWithHigherAmountPaginated(eq(amount), any());
    verifyNoMoreInteractions(customerRepository);
  }
}
