package com.clientes.cuentas.bankingservice.application.service;

import com.clientes.cuentas.bankingservice.application.command.CreateBankAccountForCustomerCommand;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.AccountTypeEntity;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.BankAccountEntity;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.repository.JpaAccountTypeRepository;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.repository.JpaBankAccountRepository;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.repository.JpaCustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class CreateBankAccountForCustomerServiceIntegrationTest {

  @Autowired
  private CreateBankAccountForCustomerService service;

  @Autowired
  private JpaCustomerRepository jpaCustomerRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private JpaAccountTypeRepository jpaAccountTypeRepository;

  @MockitoBean
  private JpaBankAccountRepository jpaBankAccountRepository;

  @Test
  void shouldRollbackCustomerCreationWhenBankAccountSaveFails() {
    String dni = "00000000Z";

    long customerCountBefore = jpaCustomerRepository.count();
    long bankAccountCountBefore = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cuenta_bancaria", Long.class);

    AccountTypeEntity accountType = new AccountTypeEntity();
    accountType.setCode("NRML");
    accountType.setName("NORMAL");
    jpaAccountTypeRepository.save(accountType);

    CreateBankAccountForCustomerCommand command = new CreateBankAccountForCustomerCommand();
    command.setCustomerDni(dni);
    command.setAccountTypeCode("NRML");
    command.setTotal(new BigDecimal("150.00"));

    when(jpaBankAccountRepository.save(any(BankAccountEntity.class)))
            .thenThrow(new RuntimeException("Simulated persistence error"));

    RuntimeException ex = assertThrows(RuntimeException.class, () -> service.execute(command));

    long customerCountAfter = jpaCustomerRepository.count();
    long bankAccountCountAfter = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cuenta_bancaria", Long.class);

    assertTrue(jpaCustomerRepository.findByDni(dni).isEmpty());
    assertEquals("Simulated persistence error", ex.getMessage());
    assertEquals(customerCountBefore, customerCountAfter);
    assertEquals(bankAccountCountBefore, bankAccountCountAfter);
    verify(jpaBankAccountRepository).save(any(BankAccountEntity.class));
  }
}

