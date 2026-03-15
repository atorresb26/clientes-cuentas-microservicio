package com.clientes.cuentas.demo.application.mapper;

import com.clientes.cuentas.demo.application.command.CreateBankAccountForCustomerCommand;
import com.clientes.cuentas.demo.domain.model.BankAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = AccountTypeMapper.class)
public interface BankAccountMapper {

  @Mapping(target = "accountType", source = "accountTypeCode")
  BankAccount toBankAccount(CreateBankAccountForCustomerCommand command);
}
