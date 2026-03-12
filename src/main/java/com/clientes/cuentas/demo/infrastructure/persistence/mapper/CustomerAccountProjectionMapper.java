package com.clientes.cuentas.demo.infrastructure.persistence.mapper;

import com.clientes.cuentas.demo.domain.model.BankAccount;
import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.infrastructure.persistence.projection.CustomerAccountRow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for working with the {@link CustomerAccountRow} projection
 */
@Mapper(componentModel = "spring")
public interface CustomerAccountProjectionMapper {

  /**
   * Map the {@link CustomerAccountRow} projection customer related fields, to a Customer domain object.
   * @param customerAccountRow the projection
   * @return the domain object {@link Customer}
   */
  @Mapping(target = "bankAccounts", ignore = true)
  Customer toCustomer(CustomerAccountRow customerAccountRow);

  /**
   * Map the {@link CustomerAccountRow} projection bank account related fields, to a Bank Account domain object.
   * @param customerAccountRow the projection
   * @return the domain object {@link BankAccount}
   */
  @Mapping(target = "accountType", source = "bankAccountType")
  BankAccount toBankAccount(CustomerAccountRow customerAccountRow);
}
