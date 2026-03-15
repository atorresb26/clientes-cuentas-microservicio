package com.clientes.cuentas.demo.infrastructure.api.mapper;

import com.clientes.cuentas.demo.domain.model.BankAccount;
import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.infrastructure.input.dto.BankAccountNoCustomerDTO;
import com.clientes.cuentas.demo.infrastructure.input.dto.CustomerAccountDTO;
import com.clientes.cuentas.demo.infrastructure.input.dto.CustomerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for work with the {@link Customer} in the infrastructure layer.
 */
@Mapper(componentModel = "spring")
public interface CustomerApiMapper {

  /**
   * Map the {@link Customer} list to a CustomerAccountDTO list.
   *
   * @param customerList the customer list
   * @return the customerAccountDTO list
   */
  List<CustomerAccountDTO> toCustomerAccountDtoList(List<Customer> customerList);

  /**
   * Map the {@link Customer} to a CustomerAccountDTO
   *
   * @param customer the customer domain object
   * @return the customerAccountDTO
   */
  @Mapping(target = "accounts", source = "bankAccounts")
  CustomerAccountDTO toCustomerAccountDto(Customer customer);

  /**
   * Map the {@link BankAccount} domain object to a BankAccountNoCustomerDTO.
   * This mapping is used internally in the {@code toCustomerAccountDto} method.
   *
   * @param bankAccount the bankAccount domain object
   * @return the bankAccountNoCustomerDTO
   */
  @Mapping(target = "accountType", expression = "java(bankAccount.getAccountType().getName())")
  BankAccountNoCustomerDTO toBankAccountNoCustomerDto(BankAccount bankAccount);

  /**
   * Maps a list of {@link Customer} entities to a list of {@link CustomerDTO}.
   *
   * @param customerList the list of {@link Customer} entities to be mapped
   * @return a list of {@link CustomerDTO} objects containing the mapped data,
   * or an empty list if the input list is empty
   */
  List<CustomerDTO> toCustomerDtoList(List<Customer> customerList);

  /**
   * Maps a {@link Customer} entity to its {@link CustomerDTO} representation.
   *
   * @param customer the {@link Customer} entity to convert
   * @return the mapped {@link CustomerDTO} instance
   */
  CustomerDTO toCustomerDto(Customer customer);
}
