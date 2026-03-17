package com.clientes.cuentas.bankingservice.api;

import com.clientes.cuentas.bankingservice.BaseIntegrationTest;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.CustomerDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomersApiIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

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
    List<CustomerDTO> customers = objectMapper.readValue(json, new TypeReference<>() {});

    LocalDate adultLimit = LocalDate.now().minusYears(18);
    customers.forEach(customer -> assertFalse(customer.getBirthDate().isAfter(adultLimit)));
  }

  /**
   * Comprueba que filtra correctamente
   *
   * @throws Exception exception
   */
  @Test
  void shouldReturnFourCustomersWhenMinBalanceIs300()  throws Exception {
    mockMvc.perform(get("/clientes/con-cuenta-superior-a/{cantidad}", new BigDecimal("300.00")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(4));
  }

  @Test
  void shouldReturnCustomProblemDetailWhenRequestBodyHasMissingField() throws Exception {
    mockMvc.perform(post("/cuentas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "dniCliente": "12345678A",
                              "total": 100.00
                            }
                            """))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.type").doesNotExist())
            .andExpect(jsonPath("$.title").value("Bad Request"))
            .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("'codTipoCuenta'")))
            .andExpect(jsonPath("$.instance").value("/cuentas"))
            .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void shouldReturnCustomProblemDetailWhenEnumValueIsInvalid() throws Exception {
    mockMvc.perform(post("/cuentas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "dniCliente": "12345678A",
                              "codTipoCuenta": "INVALIDA",
                              "total": 100.00
                            }
                            """))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.type").doesNotExist())
            .andExpect(jsonPath("$.title").value("Bad Request"))
            .andExpect(jsonPath("$.detail").value("Invalid value 'INVALIDA' for field 'codTipoCuenta'. Accepted values are: [JR, NRML, PREM]."))
            .andExpect(jsonPath("$.instance").value("/cuentas"))
            .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void shouldReturnCustomProblemDetailWhenDniFormatIsInvalid() throws Exception {
    mockMvc.perform(post("/cuentas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "dniCliente": "1234A",
                              "codTipoCuenta": "NRML",
                              "total": 100.00
                            }
                            """))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.type").doesNotExist())
            .andExpect(jsonPath("$.title").value("Bad Request"))
            .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("'dniCliente'")))
            .andExpect(jsonPath("$.instance").value("/cuentas"))
            .andExpect(jsonPath("$.timestamp").exists());
  }
}
