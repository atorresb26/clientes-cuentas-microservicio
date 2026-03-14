package com.clientes.cuentas.demo.api;

import com.clientes.cuentas.demo.infrastructure.input.dto.CustomerDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CustomersApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  /**
   * Check that it returns the original 5 customers.
   *
   * @throws Exception exception
   */
  @Test
  void shouldReturnAllCustomers() throws Exception {
    mockMvc.perform(get("/clientes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(5));
  }

  /**
   * Comprueba que devuelve solo los mayores de 18 años
   *
   * @throws Exception exception
   */
  @Test
  void shouldReturnOnlyAdultCustomers() throws Exception {
    MvcResult result = mockMvc.perform(get("/clientes/mayores-de-edad"))
            .andExpect(status().isOk())
            .andReturn();

    String json = result.getResponse().getContentAsString();

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    List<CustomerDTO> customers = mapper.readValue(json, new TypeReference<>() {
    });

    LocalDate adultLimit = LocalDate.now().minusYears(18);
    customers.forEach(customer -> assertFalse(customer.getBirthDate().isAfter(adultLimit)));
  }
}
