package com.clientes.cuentas.bankingservice.infrastructure.api.mapper;

import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.model.vo.Dni;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.BankAccountNoCustomerDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.CustomerAccountDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.CustomerDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.PaginatedCustomerAccountDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.PaginatedCustomerDTO;
import com.clientes.cuentas.bankingservice.application.pagination.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for work with the {@link Customer} in the infrastructure layer.
 */
@Mapper(componentModel = "spring", imports = Dni.class)
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
  @Mapping(target = "dni", expression = "java(customer.getDni() != null ? customer.getDni().value() : null)")
  CustomerAccountDTO toCustomerAccountDto(Customer customer);

  /**
   * Map the {@link BankAccount} domain object to a BankAccountNoCustomerDTO.
   * This mapping is used internally in the {@code toCustomerAccountDto} method.
   *
   * @param bankAccount the bankAccount domain object
   * @return the bankAccountNoCustomerDTO
   */
  @Mapping(target = "accountType", expression = "java(bankAccount.getAccountType().getName())")
  @Mapping(target = "total", expression = "java(bankAccount.getTotal() != null ? bankAccount.getTotal().amount() : null)")
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
  @Mapping(target = "dni", expression = "java(customer.getDni() != null ? customer.getDni().value() : null)")
  CustomerDTO toCustomerDto(Customer customer);

  /**
   * Maps a paginated customer result to the API paginated customer DTO.
   *
   * @param pageResult source paginated result from application layer
   * @return mapped paginated customer DTO
   */
  @Mapping(target = "content", source = "content")
  @Mapping(target = "currentPage", source = "pageNumber")
  @Mapping(target = "pageSize", source = "pageSize")
  @Mapping(target = "totalElements", source = "totalElements")
  @Mapping(target = "totalPages", source = "totalPages")
  @Mapping(target = "isFirst", expression = "java(pageResult.isFirst())")
  @Mapping(target = "isLast", expression = "java(pageResult.isLast())")
  @Mapping(target = "hasNext", expression = "java(pageResult.hasNext())")
  @Mapping(target = "hasPrevious", expression = "java(pageResult.hasPrevious())")
  PaginatedCustomerDTO toPaginatedCustomerDto(PageResult<Customer> pageResult);

  /**
   * Maps a paginated customer result to the API paginated customer-account DTO.
   *
   * @param pageResult source paginated result from application layer
   * @return mapped paginated customer-account DTO
   */
  @Mapping(target = "content", source = "content")
  @Mapping(target = "currentPage", source = "pageNumber")
  @Mapping(target = "pageSize", source = "pageSize")
  @Mapping(target = "totalElements", source = "totalElements")
  @Mapping(target = "totalPages", source = "totalPages")
  @Mapping(target = "isFirst", expression = "java(pageResult.isFirst())")
  @Mapping(target = "isLast", expression = "java(pageResult.isLast())")
  @Mapping(target = "hasNext", expression = "java(pageResult.hasNext())")
  @Mapping(target = "hasPrevious", expression = "java(pageResult.hasPrevious())")
  PaginatedCustomerAccountDTO toPaginatedCustomerAccountDto(PageResult<Customer> pageResult);
}
