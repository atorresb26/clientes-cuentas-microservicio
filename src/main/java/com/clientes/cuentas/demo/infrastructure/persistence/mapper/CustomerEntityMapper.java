package com.clientes.cuentas.demo.infrastructure.persistence.mapper;

import com.clientes.cuentas.demo.domain.model.Customer;
import com.clientes.cuentas.demo.infrastructure.persistence.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for map from {@link CustomerEntity} to {@link Customer} (domain object) and vice versa
 */
@Mapper(componentModel = "spring")
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
  Customer toCustomer(CustomerEntity customerEntity);
}
