package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.application.command.CreateBankAccountForCustomerCommand;
import com.clientes.cuentas.bankingservice.application.constants.CacheNames;
import com.clientes.cuentas.bankingservice.application.mapper.BankAccountMapper;
import com.clientes.cuentas.bankingservice.application.repository.BankAccountRepository;
import com.clientes.cuentas.bankingservice.application.repository.CustomerRepository;
import com.clientes.cuentas.bankingservice.application.usecase.CreateBankAccountForCustomerUseCase;
import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.model.vo.Dni;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service that creates a bank account for a customer.
 */
@Service
@RequiredArgsConstructor
public class CreateBankAccountForCustomerService implements CreateBankAccountForCustomerUseCase {

  private final BankAccountRepository bankAccountRepository;
  private final CustomerRepository customerRepository;

  private final BankAccountMapper bankAccountMapper;

  @Override
  @Transactional
  @CacheEvict(value = CacheNames.CUSTOMER_BY_DNI, allEntries = true)
  public BankAccount execute(CreateBankAccountForCustomerCommand command) {
    var customer = customerRepository.findByDni(command.customerDni())
            .orElseGet(() -> customerRepository.save(
                            Customer.builder()
                                    .dni(Dni.of(command.customerDni()))
                                    .build()
                    )
            );
    BankAccount bankAccount = bankAccountMapper.toBankAccount(command);
    bankAccount.assignToCustomer(customer.getId());

    return bankAccountRepository.save(bankAccount);
  }
}
