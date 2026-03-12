package com.clientes.cuentas.demo.infrastructure.port.input.delegate;

import com.clientes.cuentas.demo.application.port.input.GetCustomersUseCase;
import com.clientes.cuentas.demo.infrastructure.input.api.ClientesApiDelegate;
import com.clientes.cuentas.demo.infrastructure.input.dto.CustomerAccountDTO;
import com.clientes.cuentas.demo.infrastructure.port.input.mapper.CustomerApiMapper;
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

  private final GetCustomersUseCase  getCustomersUseCase;
  private final CustomerApiMapper customerApiMapper;

  @Override
  public ResponseEntity<List<CustomerAccountDTO>> getClients() {
    var customers = getCustomersUseCase.getCustomers();

    List<CustomerAccountDTO> response = customerApiMapper.toCustomerAccountDtoList(customers);
    return ResponseEntity.ok(response);
  }
}
