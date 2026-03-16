package com.clientes.cuentas.demo.infrastructure.api.delegate;

import com.clientes.cuentas.demo.application.usecase.CreateBankAccountForCustomerUseCase;
import com.clientes.cuentas.demo.domain.model.BankAccount;
import com.clientes.cuentas.demo.infrastructure.api.mapper.BankAccountApiMapper;
import com.clientes.cuentas.demo.infrastructure.input.api.CuentasApiDelegate;
import com.clientes.cuentas.demo.infrastructure.input.dto.BankAccountNoCustomerDTO;
import com.clientes.cuentas.demo.infrastructure.input.dto.CreateBankAccountForCustomerRequestDTO;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * Implementation of the ApiDelegate generated from the API Specification for Bank Accounts.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CuentasApiDelegateImpl implements CuentasApiDelegate {

  private final CreateBankAccountForCustomerUseCase  createBankAccountForCustomerUseCase;

  private final BankAccountApiMapper mapper;

  @Override
  @Timed(value = "bankaccount.api.createBankAccountForCustomer",
          description = "Time spent executing the createBankAccountForCustomer functionality.")
  public ResponseEntity<BankAccountNoCustomerDTO> createBankAccountForCustomer(CreateBankAccountForCustomerRequestDTO requestDTO) {
    // TODO -> Pendiente validar request body
    log.debug("- Init - createBankAccountForCustomer() with the following parameters: {}", requestDTO);

    var command = mapper.toCommand(requestDTO);
    BankAccount response = createBankAccountForCustomerUseCase.execute(command);
    // TODO -> pendiente crear endpoint consulta de detalle
    URI location = URI.create("/cuentas/" + response.getApiId());

    log.debug("- End -  createBankAccountForCustomer()");
    return ResponseEntity
            .created(location)
            .body(mapper.toDto(response));
  }
}
