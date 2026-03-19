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
   * <p>The account type code provided in the command is mapped
   * to the {@code accountType} field using {@link AccountTypeMapper}.</p>
   *
   * @param command command containing the information required
   *                to create the bank account
   * @return domain bank account object
   */
  @Mapping(target = "accountType", source = "accountTypeCode")
  @Mapping(target = "apiId", ignore = true)
  @Mapping(target = "customerId", ignore = true)
  @Mapping(target = "total", expression = "java(command.total() != null ? new Money(command.total()) : null)")
  BankAccount toBankAccount(CreateBankAccountForCustomerCommand command);
}
