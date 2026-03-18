package com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper;

import com.clientes.cuentas.bankingservice.domain.exception.AccountTypeNotFoundException;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.AccountTypeEntity;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.repository.JpaAccountTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Mapper component responsible for resolving {@link AccountTypeEntity} references
 * from a given account type code. Utilizes caching to reduce redundant database lookups.
 */
@Component
@RequiredArgsConstructor
public class AccountTypeReferenceMapper {

  private final JpaAccountTypeRepository jpaAccountTypeRepository;

  /**
   * Resolves an {@link AccountTypeEntity} from the given account type code.
   * <p>
   * Results are cached under the {@code account-types} cache using the provided
   * {@code code} as the key, avoiding redundant database queries for the same value.
   * </p>
   *
   * @param code the unique code identifying the account type; may be {@code null}
   * @return the matching {@link AccountTypeEntity}, or {@code null} if {@code code} is {@code null}
   * @throws AccountTypeNotFoundException if no account type is found for the given {@code code}
   */
  @Cacheable(value = "account-types", key = "#code", condition = "#code != null")
  public AccountTypeEntity mapFromCode(String code) {
    if (Objects.isNull(code)) {
      return null;
    }
    return jpaAccountTypeRepository.findByCode(code)
            .orElseThrow(() -> new AccountTypeNotFoundException(code));
  }
}
