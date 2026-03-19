package com.clientes.cuentas.bankingservice.infrastructure.api.delegate;

import com.clientes.cuentas.bankingservice.application.usecase.GetAdultCustomersUseCase;
import com.clientes.cuentas.bankingservice.application.usecase.GetCustomerByDniUseCase;
import com.clientes.cuentas.bankingservice.application.usecase.GetCustomersUseCase;
import com.clientes.cuentas.bankingservice.application.usecase.GetCustomersWithHigherAmountUseCase;
import com.clientes.cuentas.bankingservice.application.pagination.PaginationCriteria;
import com.clientes.cuentas.bankingservice.infrastructure.api.mapper.CustomerApiMapper;
import com.clientes.cuentas.bankingservice.infrastructure.input.api.ClientesApiDelegate;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.CustomerAccountDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.PaginatedCustomerAccountDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.PaginatedCustomerDTO;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Implementation of the ApiDelegate generated from the API Specification for Customers.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CustomersApiDelegateImpl implements ClientesApiDelegate {

  private final GetCustomersUseCase getCustomersUseCase;
  private final GetAdultCustomersUseCase getAdultCustomersUseCase;
  private final GetCustomersWithHigherAmountUseCase getCustomersWithHigherAmountUseCase;
  private final GetCustomerByDniUseCase getCustomerByDniUseCase;

  private final CustomerApiMapper mapper;

  @Override
  @Timed(value = "customer.api.getCustomersAndAccounts",
          description = "Time spent executing the getCustomersAndAccounts functionality.")
  public ResponseEntity<PaginatedCustomerAccountDTO> getCustomersAndAccounts(
          Integer page, Integer size, String sort) {
    log.debug("- Init - getCustomersAndAccounts() with page={}, size={}, sort={}", page, size, sort);

    var pageResult = getCustomersUseCase.execute(toPaginationCriteria(page, size, sort));
    var response = mapper.toPaginatedCustomerAccountDto(pageResult);

    log.debug("- End - getCustomersAndAccounts()");
    return ResponseEntity.ok(response);
  }

  @Override
  @Timed(value = "customer.api.getAdultCustomers",
          description = "Time spent executing the getAdultCustomers functionality.")
  public ResponseEntity<PaginatedCustomerDTO> getAdultCustomers(
          Integer page, Integer size, String sort) {
    log.debug("- Init - getAdultCustomers() with page={}, size={}, sort={}", page, size, sort);

    var pageResult = getAdultCustomersUseCase.execute(toPaginationCriteria(page, size, sort));
    var response = mapper.toPaginatedCustomerDto(pageResult);

    log.debug("- End - getAdultCustomers()");
    return ResponseEntity.ok(response);
  }

  @Override
  @Timed(value = "customer.api.getCustomersWithHigherAmount",
          description = "Time spent executing the getCustomersWithHigherAmount functionality.")
  public ResponseEntity<PaginatedCustomerDTO> getCustomersWithHigherAmount(
          BigDecimal cantidad, Integer page, Integer size, String sort) {
    log.debug("- Init - getCustomersWithHigherAmount() with cantidad={}, page={}, size={}, sort={}",
            cantidad, page, size, sort);

    var pageResult = getCustomersWithHigherAmountUseCase.execute(cantidad, toPaginationCriteria(page, size, sort));
    var response = mapper.toPaginatedCustomerDto(pageResult);

    log.debug("- End - getCustomersWithHigherAmount()");
    return ResponseEntity.ok(response);
  }

  @Override
  @Timed(value = "customer.api.getCustomerByDni",
          description = "Time spent executing the getCustomerByDni functionality.")
  public ResponseEntity<CustomerAccountDTO> getCustomerByDni(String dni) {
    log.debug("- Init - getCustomerByDni() with the following DNI: {}", dni);

    var customer = getCustomerByDniUseCase.execute(dni);
    var response = mapper.toCustomerAccountDto(customer);

    log.debug("- End - getCustomerByDni()");
    return ResponseEntity.ok(response);
  }

  private PaginationCriteria toPaginationCriteria(Integer page, Integer size, String sort) {
    return PaginationCriteria.builder()
            .page(page != null ? page : 0)
            .size(size != null ? size : 20)
            .sort(sort)
            .build();
  }
}
