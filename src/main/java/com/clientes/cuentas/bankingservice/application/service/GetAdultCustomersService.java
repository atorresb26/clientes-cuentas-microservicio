package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.application.repository.CustomerRepository;
import com.clientes.cuentas.bankingservice.application.usecase.GetAdultCustomersUseCase;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.application.pagination.PaginationCriteria;
import com.clientes.cuentas.bankingservice.application.pagination.PageResult;
import com.clientes.cuentas.bankingservice.application.pagination.PageResultConverter;
import com.clientes.cuentas.bankingservice.application.pagination.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service implementation for the GetAdultCustomersUseCase.
 */
@Service
@RequiredArgsConstructor
public class GetAdultCustomersService implements GetAdultCustomersUseCase {

  private final CustomerRepository customerRepository;

  @Override
  public PageResult<Customer> execute(PaginationCriteria pagination) {
    Pageable pageable = PaginationUtils.toPageable(pagination);
    Page<Customer> adultCustomersPaginated = customerRepository.getAdultCustomersPaginated(pageable);
    return PageResultConverter.fromPage(adultCustomersPaginated);
  }
}
