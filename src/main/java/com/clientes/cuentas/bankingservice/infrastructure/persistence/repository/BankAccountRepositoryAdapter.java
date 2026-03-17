package com.clientes.cuentas.bankingservice.infrastructure.persistence.repository;

import com.clientes.cuentas.bankingservice.domain.model.BankAccount;
import com.clientes.cuentas.bankingservice.domain.port.output.BankAccountRepository;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper.BankAccountEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository implementation for the BankAccount Repository port.
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class BankAccountRepositoryAdapter implements BankAccountRepository {

  private final JpaBankAccountRepository jpaBankAccountRepository;

  private final BankAccountEntityMapper mapper;

  @Override
  public BankAccount save(BankAccount bankAccount) {
    var entity = mapper.toEntity(bankAccount);
    entity.setApiId(UUID.randomUUID().toString());
    var savedEntity = jpaBankAccountRepository.save(entity);
    return mapper.toDomainObject(savedEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<BankAccount> findByApiId(UUID apiId) {
    var entity = jpaBankAccountRepository.findByApiId(apiId.toString());
    return entity.map(mapper::toDomainObject);
  }
}
