package com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper;

import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.model.vo.Dni;
import com.clientes.cuentas.bankingservice.domain.model.vo.Money;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.projection.CustomerAccountRow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for working with the {@link CustomerAccountRow} projection
 */
@Mapper(componentModel = "spring", imports = {Dni.class, Money.class})
public interface CustomerAccountProjectionMapper {

  /**
   * Map the {@link CustomerAccountRow} projection customer related fields, to a Customer domain object.
   * @param customerAccountRow the projection
   * @return the domain object {@link Customer}
   */
  @Mapping(target = "bankAccounts", ignore = true)
  @Mapping(target = "dni", expression = "java(customerAccountRow.dni() != null ? Dni.of(customerAccountRow.dni()) : null)")
  Customer toCustomer(CustomerAccountRow customerAccountRow);

  /**
   * Map the {@link CustomerAccountRow} projection bank account related fields, to a Bank Account domain object.
   * @param customerAccountRow the projection
   * @return the domain object {@link BankAccount}
   */
  @Mapping(target = "accountType", source = "bankAccountType")
  @Mapping(target = "apiId", source = "bankAccountApiId")
  @Mapping(target = "customerId", ignore = true)
  @Mapping(target = "customerDni", ignore = true)
  @Mapping(target = "total", expression = "java(customerAccountRow.total() != null ? new Money(customerAccountRow.total()) : null)")
  BankAccount toBankAccount(CustomerAccountRow customerAccountRow);
}
