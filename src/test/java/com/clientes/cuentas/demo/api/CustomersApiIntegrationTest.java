package com.clientes.cuentas.demo.api;

import com.clientes.cuentas.demo.application.port.input.GetCustomersUseCase;
import com.clientes.cuentas.demo.infrastructure.persistence.entity.AccountTypeEntity;
import com.clientes.cuentas.demo.infrastructure.persistence.entity.BankAccountEntity;
import com.clientes.cuentas.demo.infrastructure.persistence.entity.CustomerEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CustomersApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private EntityManager entityManager;

  // Replaces the actual bean in the Spring context
  @MockitoBean
  private GetCustomersUseCase getCustomersUseCase;

  @Test
  void shouldReturnEmptyListWhenNoCustomers() throws Exception {
    mockMvc.perform(get("/clientes"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
  }

  @Test
  void shouldReturnCustomersWithAccounts() throws Exception {
    AccountTypeEntity accountType = new AccountTypeEntity();
    accountType.setCode("NRML");
    accountType.setName("NORMAL");
    entityManager.persist(accountType);

    CustomerEntity customer = new CustomerEntity();
    customer.setDni("12345678A");
    customer.setName("John");
    customer.setSurname1("Doe");
    customer.setBirthDate(LocalDate.of(1990, 1, 1));
    entityManager.persist(customer);

    BankAccountEntity account = new BankAccountEntity();
    account.setCustomer(customer);
    account.setAccountType(accountType);
    account.setTotal(1000.0);
    entityManager.persist(account);

    entityManager.flush();

    mockMvc.perform(get("/clientes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].dni").value("12345678A"))
            .andExpect(jsonPath("$[0].name").value("John"))
            .andExpect(jsonPath("$[0].accounts[0].accountType").value("NORMAL"))
            .andExpect(jsonPath("$[0].accounts[0].total").value(1000.0));
  }

  @Test
  void shouldReturnProblemDetailWhenInternalError() throws Exception {
    when(getCustomersUseCase.getCustomersAndAccounts())
            .thenThrow(new RuntimeException("Database error"));

    mockMvc.perform(get("/clientes"))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentType("application/problem+json"))
            .andExpect(jsonPath("$.title").value("Internal Server Error"))
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.instance").value("/clientes"));
  }
}
