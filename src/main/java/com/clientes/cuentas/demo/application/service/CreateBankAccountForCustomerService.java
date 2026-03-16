package com.clientes.cuentas.demo.application.service;

import com.clientes.cuentas.demo.application.command.CreateBankAccountForCustomerCommand;
import com.clientes.cuentas.demo.application.mapper.BankAccountMapper;
import com.clientes.cuentas.demo.application.usecase.CreateBankAccountForCustomerUseCase;
import com.clientes.cuentas.demo.domain.model.BankAccount;
import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.domain.port.output.BankAccountRepository;
import com.clientes.cuentas.demo.domain.port.output.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateBankAccountForCustomerService implements CreateBankAccountForCustomerUseCase {

  private final BankAccountRepository bankAccountRepository;
  private final CustomerRepository customerRepository;

  private final BankAccountMapper bankAccountMapper;

  @Override
  @Transactional
  @CacheEvict(value = "customer-with-accounts", allEntries = true)
  public BankAccount execute(CreateBankAccountForCustomerCommand command) {
    var customer = customerRepository.findByDni(command.getCustomerDni())
            .orElseGet(() -> customerRepository.save(
                            Customer.builder()
                                    .dni(command.getCustomerDni())
                                    .build()
                    )
            );
    BankAccount bankAccount = bankAccountMapper.toBankAccount(command);
    bankAccount.setCustomerId(customer.getId());

    return bankAccountRepository.save(bankAccount);
  }
}
