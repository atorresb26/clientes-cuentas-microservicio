package com.clientes.cuentas.bankingservice.infrastructure.persistence.repository;

import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.AccountTypeEntity;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.BankAccountEntity;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.entity.CustomerEntity;
import com.clientes.cuentas.bankingservice.infrastructure.persistence.projection.CustomerAccountRow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    bankAccount.setApiId(UUID.randomUUID().toString());
    bankAccount.setCustomer(customer);
    bankAccount.setAccountType(accountType);
    bankAccount.setTotal(new BigDecimal("1000.00"));
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
    assertEquals(bankAccount.getTotal(), row.total());
  }

  @Test
  void shouldReturnCustomersWithHigherAmountThanGivenValue() {
    CustomerEntity c1 = new CustomerEntity();
    c1.setDni("dni1");
    c1.setName("name1");
    c1.setSurname1("surname1");
    c1.setBirthDate(LocalDate.now().minusYears(20));

    CustomerEntity c2 = new CustomerEntity();
    c2.setDni("dni2");
    c2.setName("name2");
    c2.setSurname1("surname1_2");
    c2.setBirthDate(LocalDate.now().minusYears(10));

    CustomerEntity c3 = new CustomerEntity();
    c3.setDni("dni3");
    c3.setName("name3");
    c3.setSurname1("surname1_3");
    c3.setBirthDate(LocalDate.now().minusYears(32));

    testEntityManager.persist(c1);
    testEntityManager.persist(c2);
    testEntityManager.persist(c3);

    AccountTypeEntity accountType = new AccountTypeEntity();
    accountType.setCode("NRML");
    accountType.setName("NORMAL");
    testEntityManager.persist(accountType);

    BankAccountEntity ba1 = new BankAccountEntity();
    ba1.setApiId(UUID.randomUUID().toString());
    ba1.setAccountType(accountType);
    ba1.setCustomer(c1);
    ba1.setTotal(new BigDecimal("200.0"));

    BankAccountEntity ba2 = new BankAccountEntity();
    ba2.setApiId(UUID.randomUUID().toString());
    ba2.setAccountType(accountType);
    ba2.setCustomer(c1);
    ba2.setTotal(new BigDecimal("200.0")); // total = 400

    BankAccountEntity ba3 = new BankAccountEntity();
    ba3.setApiId(UUID.randomUUID().toString());
    ba3.setAccountType(accountType);
    ba3.setCustomer(c2);
    ba3.setTotal(new BigDecimal("100.0"));

    testEntityManager.persist(ba1);
    testEntityManager.persist(ba2);
    testEntityManager.persist(ba3);

    testEntityManager.flush();

    List<CustomerEntity> result = jpaCustomerRepository.getCustomersWithHigherAmount(new BigDecimal("300.0"));

    assertEquals(1, result.size());
    assertTrue(result.contains(c1));
  }

  @Test
  void shouldReturnEmptyListWhenNoCustomerMatches() {
    CustomerEntity c1 = new CustomerEntity();
    c1.setDni("dni1");
    c1.setName("name1");
    c1.setSurname1("surname1");
    c1.setBirthDate(LocalDate.now().minusYears(20));
    testEntityManager.persist(c1);

    AccountTypeEntity accountType = new AccountTypeEntity();
    accountType.setCode("NRML");
    accountType.setName("NORMAL");
    testEntityManager.persist(accountType);

    BankAccountEntity ba = new BankAccountEntity();
    ba.setApiId(UUID.randomUUID().toString());
    ba.setCustomer(c1);
    ba.setTotal(new BigDecimal("100.0"));
    ba.setAccountType(accountType);
    testEntityManager.persist(ba);
    testEntityManager.flush();

    List<CustomerEntity> result = jpaCustomerRepository.getCustomersWithHigherAmount(new BigDecimal("300.0"));

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnCustomerWhenDniExists() {
    CustomerEntity customer1 = new CustomerEntity();
    customer1.setDni("11111111A");
    customer1.setName("name1");
    customer1.setSurname1("surname1");
    customer1.setBirthDate(LocalDate.now().minusYears(30));
    testEntityManager.persist(customer1);

    CustomerEntity customer2 = new CustomerEntity();
    customer2.setDni("22222222B");
    customer2.setName("name2");
    customer2.setSurname1("surname2");
    customer2.setBirthDate(LocalDate.now().minusYears(25));
    testEntityManager.persist(customer2);

    testEntityManager.flush();

    Optional<CustomerEntity> result = jpaCustomerRepository.findByDni("22222222B");

    assertTrue(result.isPresent());
    assertEquals(customer2.getId(), result.get().getId());
    assertEquals("22222222B", result.get().getDni());
    assertEquals("name2", result.get().getName());
  }

  @Test
  void shouldReturnCustomerWithoutAccountsInCustomersAndAccounts() {
    CustomerEntity customer = new CustomerEntity();
    customer.setDni("33333333C");
    customer.setName("name3");
    customer.setSurname1("surname3");
    customer.setBirthDate(LocalDate.of(1995, 3, 3));
    testEntityManager.persist(customer);
    testEntityManager.flush();

    List<CustomerAccountRow> result = jpaCustomerRepository.getCustomersAndAccounts();

    assertEquals(1, result.size());
    CustomerAccountRow row = result.getFirst();
    assertEquals(customer.getId(), row.id());
    assertEquals("33333333C", row.dni());
    assertEquals("name3", row.name());
    assertNull(row.bankAccountId());
    assertNull(row.bankAccountApiId());
    assertNull(row.bankAccountType());
    assertNull(row.total());
  }

  @Test
  void shouldReturnOneRowPerAccountInCustomersAndAccounts() {
    CustomerEntity customer = new CustomerEntity();
    customer.setDni("44444444D");
    customer.setName("name4");
    customer.setSurname1("surname4");
    customer.setBirthDate(LocalDate.now().minusYears(28));
    testEntityManager.persist(customer);

    AccountTypeEntity accountType = new AccountTypeEntity();
    accountType.setCode("NRML");
    accountType.setName("NORMAL");
    testEntityManager.persist(accountType);

    BankAccountEntity ba1 = new BankAccountEntity();
    ba1.setApiId(UUID.randomUUID().toString());
    ba1.setCustomer(customer);
    ba1.setAccountType(accountType);
    ba1.setTotal(new BigDecimal("120.00"));
    testEntityManager.persist(ba1);

    BankAccountEntity ba2 = new BankAccountEntity();
    ba2.setApiId(UUID.randomUUID().toString());
    ba2.setCustomer(customer);
    ba2.setAccountType(accountType);
    ba2.setTotal(new BigDecimal("80.00"));
    testEntityManager.persist(ba2);

    testEntityManager.flush();

    List<CustomerAccountRow> result = jpaCustomerRepository.getCustomersAndAccounts();

    assertEquals(2, result.size());

    Set<Long> customerIds = result.stream().map(CustomerAccountRow::id).collect(Collectors.toSet());
    Set<Long> bankAccountIds = result.stream().map(CustomerAccountRow::bankAccountId).collect(Collectors.toSet());
    Set<BigDecimal> totals = result.stream().map(CustomerAccountRow::total).collect(Collectors.toSet());

    assertEquals(Set.of(customer.getId()), customerIds);
    assertEquals(new HashSet<>(List.of(ba1.getId(), ba2.getId())), bankAccountIds);
    assertEquals(new HashSet<>(List.of(new BigDecimal("120.00"), new BigDecimal("80.00"))), totals);
  }

  @Test
  void shouldNotReturnCustomerWhenTotalAmountEqualsThreshold() {
    CustomerEntity customer = new CustomerEntity();
    customer.setDni("55555555E");
    customer.setName("name5");
    customer.setSurname1("surname5");
    customer.setBirthDate(LocalDate.now().minusYears(35));
    testEntityManager.persist(customer);

    AccountTypeEntity accountType = new AccountTypeEntity();
    accountType.setCode("NRML");
    accountType.setName("NORMAL");
    testEntityManager.persist(accountType);

    BankAccountEntity ba1 = new BankAccountEntity();
    ba1.setApiId(UUID.randomUUID().toString());
    ba1.setCustomer(customer);
    ba1.setAccountType(accountType);
    ba1.setTotal(new BigDecimal("100.00"));
    testEntityManager.persist(ba1);

    BankAccountEntity ba2 = new BankAccountEntity();
    ba2.setApiId(UUID.randomUUID().toString());
    ba2.setCustomer(customer);
    ba2.setAccountType(accountType);
    ba2.setTotal(new BigDecimal("200.00"));
    testEntityManager.persist(ba2);

    testEntityManager.flush();

    List<CustomerEntity> result = jpaCustomerRepository.getCustomersWithHigherAmount(new BigDecimal("300.00"));

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyWhenDniDoesNotExist() {
    CustomerEntity customer = new CustomerEntity();
    customer.setDni("12345678A");
    customer.setName("name1");
    customer.setSurname1("surname1");
    customer.setBirthDate(LocalDate.now().minusYears(20));
    testEntityManager.persist(customer);
    testEntityManager.flush();

    Optional<CustomerEntity> result = jpaCustomerRepository.findByDni("99999999Z");

    assertTrue(result.isEmpty());
  }

  // =========================================================================
  // findCustomerWithAccountsByDni()
  // =========================================================================

  @Test
  void shouldReturnEmptyListWhenDniNotFoundWithAccounts() {
    CustomerEntity customer = new CustomerEntity();
    customer.setDni("12345678A");
    customer.setName("John");
    customer.setSurname1("Doe");
    customer.setBirthDate(LocalDate.of(1990, 5, 15));
    testEntityManager.persist(customer);
    testEntityManager.flush();

    List<CustomerAccountRow> result =
            jpaCustomerRepository.findCustomerWithAccountsByDni("99999999Z");

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnOneRowWithAllFieldsWhenCustomerHasOneAccount() {
    CustomerEntity customer = new CustomerEntity();
    customer.setDni("11111111A");
    customer.setName("Ana");
    customer.setSurname1("Garcia");
    customer.setSurname2("Lopez");
    customer.setBirthDate(LocalDate.of(1988, 3, 22));
    testEntityManager.persist(customer);

    AccountTypeEntity accountType = new AccountTypeEntity();
    accountType.setCode("NRML");
    accountType.setName("NORMAL");
    testEntityManager.persist(accountType);

    String apiId = UUID.randomUUID().toString();
    BankAccountEntity bankAccount = new BankAccountEntity();
    bankAccount.setApiId(apiId);
    bankAccount.setCustomer(customer);
    bankAccount.setAccountType(accountType);
    bankAccount.setTotal(new BigDecimal("1500.00"));
    testEntityManager.persist(bankAccount);

    testEntityManager.flush();

    List<CustomerAccountRow> result =
            jpaCustomerRepository.findCustomerWithAccountsByDni("11111111A");

    assertEquals(1, result.size());
    CustomerAccountRow row = result.getFirst();

    // customer fields
    assertEquals(customer.getId(), row.id());
    assertEquals("11111111A", row.dni());
    assertEquals("Ana", row.name());
    assertEquals("Garcia", row.surname1());
    assertEquals("Lopez", row.surname2());
    assertEquals(LocalDate.of(1988, 3, 22), row.birthDate());

    // account fields
    assertEquals(bankAccount.getId(), row.bankAccountId());
    assertEquals(apiId, row.bankAccountApiId());
    assertEquals("NORMAL", row.bankAccountType());
    assertEquals(new BigDecimal("1500.00"), row.total());
  }

  @Test
  void shouldReturnOneRowPerAccountWhenCustomerHasMultipleAccounts() {
    CustomerEntity customer = new CustomerEntity();
    customer.setDni("22222222B");
    customer.setName("Luis");
    customer.setSurname1("Martinez");
    customer.setBirthDate(LocalDate.of(1985, 7, 10));
    testEntityManager.persist(customer);

    AccountTypeEntity accountType = new AccountTypeEntity();
    accountType.setCode("PREM");
    accountType.setName("PREMIUM");
    testEntityManager.persist(accountType);

    BankAccountEntity ba1 = new BankAccountEntity();
    ba1.setApiId(UUID.randomUUID().toString());
    ba1.setCustomer(customer);
    ba1.setAccountType(accountType);
    ba1.setTotal(new BigDecimal("200.00"));
    testEntityManager.persist(ba1);

    BankAccountEntity ba2 = new BankAccountEntity();
    ba2.setApiId(UUID.randomUUID().toString());
    ba2.setCustomer(customer);
    ba2.setAccountType(accountType);
    ba2.setTotal(new BigDecimal("800.00"));
    testEntityManager.persist(ba2);

    testEntityManager.flush();

    List<CustomerAccountRow> result =
            jpaCustomerRepository.findCustomerWithAccountsByDni("22222222B");

    // one row per account
    assertEquals(2, result.size());

    // all rows belong to the same customer
    assertTrue(result.stream().allMatch(r -> r.id().equals(customer.getId())));
    assertTrue(result.stream().allMatch(r -> "22222222B".equals(r.dni())));

    // each row has a distinct bank account
    Set<Long> bankAccountIds = result.stream()
            .map(CustomerAccountRow::bankAccountId)
            .collect(Collectors.toSet());
    assertEquals(new HashSet<>(List.of(ba1.getId(), ba2.getId())), bankAccountIds);

    Set<BigDecimal> totals = result.stream()
            .map(CustomerAccountRow::total)
            .collect(Collectors.toSet());
    assertEquals(new HashSet<>(List.of(new BigDecimal("200.00"), new BigDecimal("800.00"))), totals);
  }

  @Test
  void shouldReturnRowWithNullAccountFieldsWhenCustomerHasNoAccounts() {
    CustomerEntity customer = new CustomerEntity();
    customer.setDni("33333333C");
    customer.setName("Maria");
    customer.setSurname1("Ruiz");
    customer.setBirthDate(LocalDate.of(1992, 11, 5));
    testEntityManager.persist(customer);
    testEntityManager.flush();

    List<CustomerAccountRow> result =
            jpaCustomerRepository.findCustomerWithAccountsByDni("33333333C");

    // LEFT JOIN → one row with customer data but null account fields
    assertEquals(1, result.size());
    CustomerAccountRow row = result.getFirst();

    assertEquals(customer.getId(), row.id());
    assertEquals("33333333C", row.dni());
    assertEquals("Maria", row.name());
    assertNull(row.bankAccountId());
    assertNull(row.bankAccountApiId());
    assertNull(row.bankAccountType());
    assertNull(row.total());
  }

  @Test
  void shouldReturnOnlyRowsForSearchedDniIgnoringOtherCustomers() {
    CustomerEntity target = new CustomerEntity();
    target.setDni("44444444D");
    target.setName("Carlos");
    target.setSurname1("Fernandez");
    target.setBirthDate(LocalDate.of(1978, 1, 30));
    testEntityManager.persist(target);

    CustomerEntity other = new CustomerEntity();
    other.setDni("55555555E");
    other.setName("Elena");
    other.setSurname1("Gomez");
    other.setBirthDate(LocalDate.of(1995, 6, 18));
    testEntityManager.persist(other);

    AccountTypeEntity accountType = new AccountTypeEntity();
    accountType.setCode("JR");
    accountType.setName("JUNIOR");
    testEntityManager.persist(accountType);

    BankAccountEntity baTarget = new BankAccountEntity();
    baTarget.setApiId(UUID.randomUUID().toString());
    baTarget.setCustomer(target);
    baTarget.setAccountType(accountType);
    baTarget.setTotal(new BigDecimal("300.00"));
    testEntityManager.persist(baTarget);

    BankAccountEntity baOther = new BankAccountEntity();
    baOther.setApiId(UUID.randomUUID().toString());
    baOther.setCustomer(other);
    baOther.setAccountType(accountType);
    baOther.setTotal(new BigDecimal("999.00"));
    testEntityManager.persist(baOther);

    testEntityManager.flush();

    List<CustomerAccountRow> result =
            jpaCustomerRepository.findCustomerWithAccountsByDni("44444444D");

    assertEquals(1, result.size());
    CustomerAccountRow row = result.getFirst();
    assertEquals(target.getId(), row.id());
    assertEquals("44444444D", row.dni());
    assertEquals(baTarget.getId(), row.bankAccountId());
    assertEquals(new BigDecimal("300.00"), row.total());
  }
}
