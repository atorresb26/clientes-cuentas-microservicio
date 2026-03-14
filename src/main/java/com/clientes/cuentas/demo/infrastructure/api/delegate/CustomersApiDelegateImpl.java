package com.clientes.cuentas.demo.infrastructure.api.delegate;

import com.clientes.cuentas.demo.application.port.input.GetCustomersUseCase;
import com.clientes.cuentas.demo.infrastructure.api.mapper.CustomerApiMapper;
import com.clientes.cuentas.demo.infrastructure.input.api.ClientesApiDelegate;
import com.clientes.cuentas.demo.infrastructure.input.dto.CustomerAccountDTO;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the ApiDelegate generated from the API specification.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomersApiDelegateImpl implements ClientesApiDelegate {

  private final GetCustomersUseCase getCustomersUseCase;
  private final CustomerApiMapper customerApiMapper;

  @Override
  @Timed(value = "customer.usecase.getCustomersAndAccounts",
          description = "Time spent executing getCustomersAndAccounts use case.")
  public ResponseEntity<List<CustomerAccountDTO>> getCustomersAndAccounts() {
    log.info("- Init - getCustomersAndAccounts()");
    var customers = getCustomersUseCase.getCustomersAndAccounts();

    var response = customerApiMapper.toCustomerAccountDtoList(customers);
    log.info("- End - getCustomersAndAccounts()");
    return ResponseEntity.ok(response);
  }
}
