package com.clientes.cuentas.bankingservice.application.mapper;

import com.clientes.cuentas.bankingservice.application.command.CreateBankAccountForCustomerCommand;
import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.model.vo.Money;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper responsible for converting application commands into
 * {@link BankAccount} domain objects.
 */
@Mapper(componentModel = "spring", uses = AccountTypeMapper.class, imports = Money.class)
public interface BankAccountMapper {

  /**
   * Converts the command used to create a bank account into the
   * corresponding domain {@link BankAccount} object.
   *
   * @param command command containing the information required
   *                to create the bank account
   * @return domain bank account object
   */
  @Mapping(target = "apiId", ignore = true)
  @Mapping(target = "customerId", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "accountType", source = "accountTypeCode")
  @Mapping(target = "total", expression = "java(Money.of(command.total()))")
  BankAccount toBankAccount(CreateBankAccountForCustomerCommand command);
}
