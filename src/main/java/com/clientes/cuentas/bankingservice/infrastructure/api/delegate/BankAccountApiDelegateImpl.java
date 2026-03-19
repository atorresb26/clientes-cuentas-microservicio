package com.clientes.cuentas.bankingservice.infrastructure.api.delegate;

import com.clientes.cuentas.bankingservice.application.usecase.CreateBankAccountForCustomerUseCase;
import com.clientes.cuentas.bankingservice.application.usecase.GetBankAccountDetailUseCase;
import com.clientes.cuentas.bankingservice.application.usecase.UpdateBankAccountTotalUseCase;
import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.infrastructure.api.mapper.BankAccountApiMapper;
import com.clientes.cuentas.bankingservice.infrastructure.input.api.CuentasApiDelegate;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.BankAccountDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.BankAccountNoCustomerDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.CreateBankAccountForCustomerRequestDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.UpdateBankAccountTotalRequestDTO;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

/**
 * Implementation of the ApiDelegate generated from the API Specification for Bank Accounts.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class BankAccountApiDelegateImpl implements CuentasApiDelegate {

  private final CreateBankAccountForCustomerUseCase createBankAccountForCustomerUseCase;
  private final GetBankAccountDetailUseCase getBankAccountDetailUseCase;
  private final UpdateBankAccountTotalUseCase updateBankAccountTotalUseCase;

  private final BankAccountApiMapper mapper;

  @Override
  @Timed(value = "bankaccount.api.createBankAccountForCustomer",
          description = "Time spent executing the createBankAccountForCustomer functionality.")
  public ResponseEntity<BankAccountNoCustomerDTO> createBankAccountForCustomer(CreateBankAccountForCustomerRequestDTO requestDTO) {
    log.debug("- Init - createBankAccountForCustomer() with the following parameters: {}", requestDTO);

    var command = mapper.toCommand(requestDTO);
    BankAccount response = createBankAccountForCustomerUseCase.execute(command);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{apiId}")
            .buildAndExpand(response.getApiId())
            .toUri();

    log.debug("- End -  createBankAccountForCustomer()");
    return ResponseEntity
            .created(location)
            .body(mapper.toNoCustomerDto(response));
  }

  @Override
  @Timed(value = "bankaccount.api.getBankAccountByApiId",
          description = "Time spent executing the getBankAccountByApiId functionality.")
  public ResponseEntity<BankAccountDTO> getBankAccountByApiId(UUID bankAccountApiId) {
    log.debug("- Init - getBankAccountByApiId() with the following API ID: {}", bankAccountApiId);

    var bankAccount = getBankAccountDetailUseCase.execute(bankAccountApiId);
    var response = mapper.toDto(bankAccount);

    log.debug("- End - getBankAccountByApiId()");
    return ResponseEntity.ok(response);
  }

  @Override
  @Timed(value = "bankaccount.api.updateBankAccountTotal",
          description = "Time spent executing the updateBankAccountTotal functionality.")
  public ResponseEntity<Void> updateBankAccountTotal(UUID accountApiId, UpdateBankAccountTotalRequestDTO requestDTO) {
    log.debug("- Init - updateBankAccountTotal() with the following parameters: API ID: {}, total: {}",
            accountApiId, requestDTO.getTotal());

    updateBankAccountTotalUseCase.execute(accountApiId, requestDTO.getTotal());

    log.debug("- End - updateBankAccountTotal()");
    return ResponseEntity.noContent().build();
  }
}
