package com.clientes.cuentas.demo.infrastructure.api.mapper;

import com.clientes.cuentas.demo.application.command.CreateBankAccountForCustomerCommand;
import com.clientes.cuentas.demo.domain.model.BankAccount;
import com.clientes.cuentas.demo.infrastructure.input.dto.BankAccountNoCustomerDTO;
import com.clientes.cuentas.demo.infrastructure.input.dto.CreateBankAccountForCustomerRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BankAccountApiMapper {

  @Mapping(target = "customerDni", source = "dniCliente")
  @Mapping(target = "accountTypeCode", source = "codTipoCuenta")
  CreateBankAccountForCustomerCommand toCommand(CreateBankAccountForCustomerRequestDTO requestDTO);

  BankAccountNoCustomerDTO toDto(BankAccount response);
}
