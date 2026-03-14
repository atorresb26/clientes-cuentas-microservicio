package com.clientes.cuentas.demo.infrastructure.persistence.repository;

import com.clientes.cuentas.demo.infrastructure.persistence.entity.AccountTypeEntity;
import com.clientes.cuentas.demo.infrastructure.persistence.entity.BankAccountEntity;
import com.clientes.cuentas.demo.infrastructure.persistence.entity.CustomerEntity;
import com.clientes.cuentas.demo.infrastructure.persistence.projection.CustomerAccountRow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class JpaCustomerRepositoryTest {

  @Autowired
  private JpaCustomerRepository jpaCustomerRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @Test
  void shouldReturnCustomersWithAccounts() {
    // given
    CustomerEntity customer = new CustomerEntity();
    customer.setDni("12345678A");
    customer.setName("John");
    customer.setSurname1("Doe");
    customer.setSurname2("Smith");
    customer.setBirthDate(LocalDate.of(1990, 1, 1));
    testEntityManager.persist(customer);

    AccountTypeEntity accountType = new AccountTypeEntity();
    accountType.setName("NORMAL");
    accountType.setCode("NRML");
    testEntityManager.persist(accountType);

    BankAccountEntity bankAccount = new BankAccountEntity();
    bankAccount.setCustomer(customer);
    bankAccount.setAccountType(accountType);
    bankAccount.setTotal(1000.0);
    testEntityManager.persist(bankAccount);

    testEntityManager.flush();

    // when
    List<CustomerAccountRow> result = jpaCustomerRepository.getCustomersAndAccounts();

    // then
    assertEquals(1, result.size());

    CustomerAccountRow row = result.getFirst();

    assertEquals(customer.getId(), row.id());
    assertEquals("12345678A", row.dni());
    assertEquals("John", row.name());
    assertEquals("Doe", row.surname1());
    assertEquals("Smith", row.surname2());
    assertEquals(LocalDate.of(1990,1,1), row.birthDate());

    assertEquals(bankAccount.getId(), row.bankAccountId());
    assertEquals("NORMAL", row.bankAccountType());
    assertEquals(1000.0, row.total());
  }
}
