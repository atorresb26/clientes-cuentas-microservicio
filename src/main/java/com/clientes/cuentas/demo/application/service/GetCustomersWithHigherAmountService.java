package com.clientes.cuentas.demo.application.service;

import com.clientes.cuentas.demo.application.usecase.GetCustomersWithHigherAmountUseCase;
import com.clientes.cuentas.demo.domain.exception.InvalidAmountException;
import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.domain.port.output.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetCustomersWithHigherAmountService implements GetCustomersWithHigherAmountUseCase {

  private final CustomerRepository customerRepository;

  @Override
  public List<Customer> execute(Double amount) {
    if (Objects.isNull(amount) || amount < 0) {
      log.error("Invalid amount provided, should be greater or equals than zero. Amount value: {}", amount);
      throw new InvalidAmountException("La cantidad debe ser mayor o igual a 0");
    }
    return customerRepository.getCustomersWithHigherAmount(amount);
  }
}
