package com.clientes.cuentas.demo.infrastructure.api.delegate;

import com.clientes.cuentas.demo.infrastructure.input.api.CuentasApiDelegate;
import com.clientes.cuentas.demo.infrastructure.input.dto.CreateBankAccountForCustomerRequestDTO;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Implementation of the ApiDelegate generated from the API Specification for Bank Accounts.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CuentasApiDelegateImpl implements CuentasApiDelegate {

  @Override
  @Timed(value = "bankaccount.api.createBankAccountForCustomer",
          description = "Time spent executing the createBankAccountForCustomer functionality.")
  public ResponseEntity<Void> createBankAccountForCustomer(CreateBankAccountForCustomerRequestDTO requestDTO) {
    log.info("- Init - createBankAccountForCustomer() with the following parameters: {}", requestDTO);

    log.info("- End -  createBankAccountForCustomer()");
    return CuentasApiDelegate.super.createBankAccountForCustomer(requestDTO);
  }
}
