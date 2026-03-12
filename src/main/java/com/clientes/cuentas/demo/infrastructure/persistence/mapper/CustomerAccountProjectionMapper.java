package com.clientes.cuentas.demo.infrastructure.persistence.mapper;

import com.clientes.cuentas.demo.domain.model.BankAccount;
import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.infrastructure.persistence.projection.CustomerAccountRow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerAccountProjectionMapper {

  @Mapping(target = "bankAccounts", ignore = true)
  Customer toCustomer(CustomerAccountRow customerAccountRow);

  @Mapping(target = "accountType", source = "bankAccountType")
  BankAccount toBankAccount(CustomerAccountRow customerAccountRow);
}
