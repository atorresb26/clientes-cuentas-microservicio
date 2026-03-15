package com.clientes.cuentas.demo.infrastructure.persistence.mapper;

import com.clientes.cuentas.demo.application.mapper.AccountTypeMapper;
import com.clientes.cuentas.demo.domain.model.BankAccount;
import com.clientes.cuentas.demo.infrastructure.persistence.entity.BankAccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { AccountTypeMapper.class, CustomerReferenceMapper.class, AccountTypeReferenceMapper.class })
public interface BankAccountEntityMapper {

  @Mapping(target = "customer", source = "customerId")
  @Mapping(target = "accountType", source = "accountType.code")
  BankAccountEntity toEntity(BankAccount bankAccount);

  BankAccount toDomainObject(BankAccountEntity savedEntity);
}
