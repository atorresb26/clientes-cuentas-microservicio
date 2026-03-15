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
 * Implementation of the ApiDelegate generated from the API Specification for Customers.
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
  @Timed(value = "customer.api.getCustomersAndAccounts",
          description = "Time spent executing the getCustomersAndAccounts functionality.")
  public ResponseEntity<List<CustomerAccountDTO>> getCustomersAndAccounts() {
    log.info("- Init - getCustomersAndAccounts()");
    var customers = getCustomersUseCase.execute();

    var response = mapper.toCustomerAccountDtoList(customers);
    log.info("- End - getCustomersAndAccounts()");
    return ResponseEntity.ok(response);
  }

  @Override
  @Timed(value = "customer.api.getAdultCustomers",
          description = "Time spent executing the getAdultCustomers functionality.")
  public ResponseEntity<List<CustomerDTO>> getAdultCustomers() {
    log.info("- Init - getAdultCustomers()");
    var customers = getAdultCustomersUseCase.execute();

    var response = mapper.toCustomerDtoList(customers);
    log.info("- End - getAdultCustomers()");
    return ResponseEntity.ok(response);
  }

  @Override
  @Timed(value = "customer.api.getCustomersWithHigherAmount",
          description = "Time spent executing the getCustomersWithHigherAmount functionality.")
  public ResponseEntity<List<CustomerDTO>> getCustomersWithHigherAmount(Double cantidad) {
    log.info("- Init - getCustomersWithHigherAmount() with 'cantidad' parameter: {}", cantidad);
    var customers = getCustomersWithHigherAmountUseCase.execute(cantidad);

    var response = mapper.toCustomerDtoList(customers);
    log.info("- End - getCustomersWithHigherAmount()");
    return ResponseEntity.ok(response);
  }
}
