package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.application.command.CreateBankAccountForCustomerCommand;
import com.clientes.cuentas.bankingservice.application.constants.CacheNames;
import com.clientes.cuentas.bankingservice.application.mapper.BankAccountMapper;
import com.clientes.cuentas.bankingservice.application.repository.BankAccountRepository;
import com.clientes.cuentas.bankingservice.application.repository.CustomerRepository;
import com.clientes.cuentas.bankingservice.application.usecase.CreateBankAccountForCustomerUseCase;
import com.clientes.cuentas.bankingservice.domain.enums.AccountType;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidAccountTypeCodeException;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidAmountException;
import com.clientes.cuentas.bankingservice.domain.exception.InvalidCustomerDniException;
import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.model.vo.Dni;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Application service that creates a bank account for a customer.
 */
@Service
@RequiredArgsConstructor
public class CreateBankAccountForCustomerService implements CreateBankAccountForCustomerUseCase {

  private static final Pattern DNI_PATTERN = Pattern.compile("^[0-9]{8}[A-Za-z]$");

  private final BankAccountRepository bankAccountRepository;
  private final CustomerRepository customerRepository;

  private final BankAccountMapper bankAccountMapper;

  @Override
  @Transactional
  @CacheEvict(value = CacheNames.CUSTOMER_WITH_ACCOUNTS, allEntries = true)
  public BankAccount execute(CreateBankAccountForCustomerCommand command) {
    validateCommand(command);

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

  private static void validateCommand(CreateBankAccountForCustomerCommand command) {
    validateCustomerDni(command.customerDni());
    validateAccountTypeCode(command.accountTypeCode());
    validateTotal(command.total());
  }

  private static void validateCustomerDni(String customerDni) {
    if (Objects.isNull(customerDni) || !DNI_PATTERN.matcher(customerDni).matches()) {
      throw new InvalidCustomerDniException("The customer DNI format is invalid. It must match 8 digits and 1 letter");
    }
  }

  private static void validateAccountTypeCode(String accountTypeCode) {
    if (Objects.isNull(accountTypeCode)) {
      throw new InvalidAccountTypeCodeException(
              String.format("Invalid account type code. Accepted values are: %s", AccountType.getAcceptedCodesMessage())
      );
    }

    if (!isValidAccountTypeCode(accountTypeCode)) {
      throw new InvalidAccountTypeCodeException(
              String.format("Invalid account type code '%s'. Accepted values are: %s", accountTypeCode, AccountType.getAcceptedCodesMessage())
      );
    }
  }

  private static boolean isValidAccountTypeCode(String accountTypeCode) {
    return AccountType.isValidCode(accountTypeCode);
  }

  private static void validateTotal(BigDecimal total) {
    if (Objects.isNull(total) || total.compareTo(BigDecimal.ZERO) < 0) {
      throw new InvalidAmountException("The amount must be greater than or equal to 0");
    }
  }
}
