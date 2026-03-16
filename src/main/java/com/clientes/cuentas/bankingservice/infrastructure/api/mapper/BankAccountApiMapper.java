package com.clientes.cuentas.bankingservice.infrastructure.api.mapper;

import com.clientes.cuentas.bankingservice.application.command.CreateBankAccountForCustomerCommand;
import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.BankAccountNoCustomerDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.CreateBankAccountForCustomerRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper responsible for converting between API layer DTOs and
 * domain objects related to bank accounts.
 */
@Mapper(componentModel = "spring")
public interface BankAccountApiMapper {

  /**
   * Converts the incoming API request DTO into a command object
   * used by the application use case responsible for creating
   * a bank account for a customer.
   *
   * @param requestDTO DTO received from the API request body
   * @return command object containing the data required to execute
   * the use case for creating a bank account
   */
  @Mapping(target = "customerDni", source = "dniCliente")
  @Mapping(target = "accountTypeCode", source = "codTipoCuenta")
  CreateBankAccountForCustomerCommand toCommand(CreateBankAccountForCustomerRequestDTO requestDTO);

  /**
   * Converts a domain {@link BankAccount} object into a DTO suitable
   * for returning in API responses.
   *
   * @param response domain bank account object
   * @return DTO representation of the bank account for API responses
   */
  BankAccountNoCustomerDTO toDto(BankAccount response);
}
