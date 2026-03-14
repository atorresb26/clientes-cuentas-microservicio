package com.clientes.cuentas.demo.infrastructure.api.delegate;

import com.clientes.cuentas.demo.application.usecase.GetAdultCustomersUseCase;
import com.clientes.cuentas.demo.application.usecase.GetCustomersUseCase;
import com.clientes.cuentas.demo.application.usecase.GetCustomersWithHigherAmountUseCase;
import com.clientes.cuentas.demo.infrastructure.api.mapper.CustomerApiMapper;
import com.clientes.cuentas.demo.infrastructure.input.api.ClientesApiDelegate;
import com.clientes.cuentas.demo.infrastructure.input.dto.CustomerAccountDTO;
import com.clientes.cuentas.demo.infrastructure.input.dto.CustomerDTO;
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
  private final GetAdultCustomersUseCase getAdultCustomersUseCase;
  private final GetCustomersWithHigherAmountUseCase getCustomersWithHigherAmountUseCase;

  private final CustomerApiMapper mapper;

  @Override
  @Timed(value = "customer.usecase.getCustomersAndAccounts",
          description = "Time spent executing getCustomersAndAccounts use case.")
  public ResponseEntity<List<CustomerAccountDTO>> getCustomersAndAccounts() {
    log.info("- Init - getCustomersAndAccounts()");
    var customers = getCustomersUseCase.execute();

    var response = mapper.toCustomerAccountDtoList(customers);
    log.info("- End - getCustomersAndAccounts()");
    return ResponseEntity.ok(response);
  }

  @Override
  @Timed(value = "customer.usecase.getAdultCustomers",
          description = "Time spent executing getAdultCustomers use case.")
  public ResponseEntity<List<CustomerDTO>> getAdultCustomers() {
    log.info("- Init - getAdultCustomers()");
    var customers = getAdultCustomersUseCase.execute();

    var response = mapper.toCustomerDtoList(customers);
    log.info("- End - getAdultCustomers()");
    return ResponseEntity.ok(response);
  }

  @Override
  @Timed(value = "customer.usecase.getCustomersWithHigherAmount",
          description = "Time spent executing getCustomersWithHigherAmount use case.")
  public ResponseEntity<List<CustomerDTO>> getCustomersWithHigherAmount(Double cantidad) {
    log.info("- Init - getCustomersWithHigherAmount()");
    var customers = getCustomersWithHigherAmountUseCase.execute(cantidad);

    var response = mapper.toCustomerDtoList(customers);
    log.info("- End - getCustomersWithHigherAmount()");
    return ResponseEntity.ok(response);
  }
}
