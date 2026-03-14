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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class JpaCustomerRepositoryTest {

  @Autowired
  private JpaCustomerRepository jpaCustomerRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @Test
  void shouldReturnEmptyListWhenNoCustomers() {
    List<CustomerAccountRow> result = jpaCustomerRepository.getCustomersAndAccounts();
    assertEquals(0, result.size());
  }

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
    assertEquals(LocalDate.of(1990, 1, 1), row.birthDate());

    assertEquals(bankAccount.getId(), row.bankAccountId());
    assertEquals("NORMAL", row.bankAccountType());
    assertEquals(1000.0, row.total());
  }

  @Test
  void shouldReturnCustomersOlderThan18() {
    LocalDate today = LocalDate.now();
    LocalDate adultDate = today.minusYears(18);

    CustomerEntity adult = new CustomerEntity();
    adult.setDni("dni1");
    adult.setName("name1");
    adult.setSurname1("surname1_1");
    adult.setBirthDate(today.minusYears(20));
    testEntityManager.persist(adult);

    CustomerEntity minor = new CustomerEntity();
    minor.setDni("dni2");
    minor.setName("name2");
    minor.setSurname1("surname1_2");
    minor.setBirthDate(today.minusYears(10));
    testEntityManager.persist(minor);

    testEntityManager.flush();

    List<CustomerEntity> result =
            jpaCustomerRepository.getCustomersByBirthDateLessThanEqual(adultDate);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getBirthDate()).isEqualTo(today.minusYears(20));
  }

  @Test
  void shouldIncludeCustomerTurning18Today() {
    LocalDate today = LocalDate.now();
    LocalDate adultDate = today.minusYears(18);

    CustomerEntity customer = new CustomerEntity();
    customer.setDni("dni1");
    customer.setName("name");
    customer.setSurname1("surname1");
    customer.setBirthDate(adultDate);

    testEntityManager.persist(customer);
    testEntityManager.flush();

    List<CustomerEntity> result =
            jpaCustomerRepository.getCustomersByBirthDateLessThanEqual(adultDate);

    assertThat(result).hasSize(1);
  }

  @Test
  void shouldReturnEmptyListWhenNoAdults() {
    LocalDate today = LocalDate.now();
    LocalDate adultDate = today.minusYears(18);

    CustomerEntity minor = new CustomerEntity();
    minor.setDni("dni1");
    minor.setName("name1");
    minor.setSurname1("surname1");
    minor.setBirthDate(today.minusYears(10));

    testEntityManager.persist(minor);
    testEntityManager.flush();

    List<CustomerEntity> result =
            jpaCustomerRepository.getCustomersByBirthDateLessThanEqual(adultDate);

    assertThat(result).isEmpty();
  }
}
