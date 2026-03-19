package com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper;

import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.domain.model.vo.Dni;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for map from {@link CustomerEntity} to {@link Customer} (domain object) and vice versa
 */
@Mapper(componentModel = "spring", imports = Dni.class)
public interface CustomerEntityMapper {

  /**
   * Maps a CustomerEntity object list to a Customer domain object list.
   *
   * @param customerEntities the customer entity list
   * @return the customer domain object list
   */
  List<Customer> toCustomerList(List<CustomerEntity> customerEntities);

  /**
   * Maps a CustomerEntity object to a Customer domain object.
   *
   * @param customerEntity the customer entity
   * @return the customer domain object
   */
  @Mapping(target = "bankAccounts", ignore = true)
  @Mapping(target = "dni", expression = "java(customerEntity.getDni() != null ? Dni.of(customerEntity.getDni()) : null)")
  Customer toCustomer(CustomerEntity customerEntity);

  /**
   * Maps a Customer domain object to a CustomerEntity object.
   *
   * @param customer the customer domain object
   * @return the customer entity
   */
  @Mapping(target = "dni", expression = "java(customer.getDni() != null ? customer.getDni().value() : null)")
  CustomerEntity toEntity(Customer customer);
}
