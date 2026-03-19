package com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper;

import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.model.vo.Money;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.BankAccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper responsible for converting between {@link BankAccount} domain objects
 * and {@link BankAccountEntity} persistence entities.
 */
@Mapper(componentModel = "spring", uses = {
        AccountTypeEntityMapper.class,
        CustomerReferenceMapper.class,
        AccountTypeReferenceMapper.class
}, imports = Money.class)
public interface BankAccountEntityMapper {

  /**
   * Converts a {@link BankAccount} domain object into its persistence
   * representation {@link BankAccountEntity}.
   *
   * @param bankAccount domain bank account to be mapped
   * @return persistence entity representing the bank account
   */
  @Mapping(target = "customer", source = "customerId")
  @Mapping(target = "accountType", source = "accountType.code")
  @Mapping(target = "total", expression = "java(bankAccount.getTotal() != null ? bankAccount.getTotal().amount() : null)")
  BankAccountEntity toEntity(BankAccount bankAccount);

  /**
   * Converts a {@link BankAccountEntity} persistence entity into the
   * corresponding {@link BankAccount} domain object.
   *
   * @param savedEntity persistence entity retrieved from the database
   * @return domain representation of the bank account
   */
  @Mapping(target = "customerId", source = "customer.id")
  @Mapping(target = "customerDni", source = "customer.dni")
  @Mapping(target = "total", expression = "java(savedEntity.getTotal() != null ? new Money(savedEntity.getTotal()) : null)")
  BankAccount toDomainObject(BankAccountEntity savedEntity);
}
