package com.clientes.cuentas.demo.infrastructure.port.input.mapper;

import com.clientes.cuentas.demo.domain.model.BankAccount;
import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.infrastructure.input.dto.BankAccountNoCustomerDTO;
import com.clientes.cuentas.demo.infrastructure.input.dto.CustomerAccountDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerApiMapper {


  List<CustomerAccountDTO> toCustomerAccountDtoList(List<Customer> customerList);

  @Mapping(target = "accounts", source = "bankAccounts")
  CustomerAccountDTO toCustomerAccountDto(Customer customer);

  // TODO revisar
  BankAccountNoCustomerDTO toBankAccountNoCustomerDto(BankAccount bankAccount);
}
