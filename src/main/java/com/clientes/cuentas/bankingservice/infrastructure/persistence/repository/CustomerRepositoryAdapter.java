package com.clientes.cuentas.bankingservice.infrastructure.persistence.repository;

import com.clientes.cuentas.bankingservice.application.repository.CustomerRepository;
import com.clientes.cuentas.bankingservice.domain.model.Customer;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper.CustomerAccountAssembler;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.mapper.CustomerEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository implementation for the Customer Repository port.
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

  private final JpaCustomerRepository jpaCustomerRepository;
  private final CustomerAccountAssembler customerAccountAssembler;
  private final CustomerEntityMapper mapper;

  @Override
  @Transactional(readOnly = true)
  public Page<Customer> getCustomersAndAccountsPaginated(Pageable pageable) {
    var page = jpaCustomerRepository.getCustomersAndAccountsPaginated(pageable);
    log.debug("- getCustomersAndAccountsPaginated search returns {} results in page {}.", 
        page.getContent().size(), page.getNumber());
    var customers = customerAccountAssembler.toCustomers(page.getContent());
    return new PageImpl<>(customers, pageable, page.getTotalElements());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Customer> getAdultCustomersPaginated(Pageable pageable) {
    var page = jpaCustomerRepository.getCustomersByBirthDateLessThanEqualPaginated(LocalDate.now().minusYears(18), pageable);
    log.debug("- getAdultCustomersPaginated search returns {} results in page {}", 
        page.getContent().size(), page.getNumber());
    var customers = mapper.toCustomerList(page.getContent());
    return new PageImpl<>(customers, pageable, page.getTotalElements());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Customer> getCustomersWithHigherAmountPaginated(BigDecimal amount, Pageable pageable) {
    var page = jpaCustomerRepository.getCustomersWithHigherAmountPaginated(amount, pageable);
    log.debug("- getCustomersWithHigherAmountPaginated search returns {} results in page {} for amount: {}", 
        page.getContent().size(), page.getNumber(), amount);
    var customers = mapper.toCustomerList(page.getContent());
    return new PageImpl<>(customers, pageable, page.getTotalElements());
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Customer> findByDni(String dni) {
    log.debug("- findByDni - Searching for customer with dni {}", dni);
    return jpaCustomerRepository.findByDni(dni)
            .map(mapper::toCustomer);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Customer> findByDniWithAccounts(String dni) {
    log.debug("- findByDniWithAccounts - Searching for customer with accounts for dni {}", dni);
    var rows = jpaCustomerRepository.findCustomerWithAccountsByDni(dni);
    return customerAccountAssembler.toCustomers(rows).stream().findFirst();
  }

  @Override
  public Customer save(Customer customer) {
    var customerEntity = mapper.toEntity(customer);
    log.debug("Saving customer entity with the following data: {}", customerEntity);
    var savedEntity = jpaCustomerRepository.save(customerEntity);
    log.debug("The customer was successfully saved. Saved entity data: {}", savedEntity);
    return mapper.toCustomer(savedEntity);
  }
}
