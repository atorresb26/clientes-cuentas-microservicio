package com.clientes.cuentas.bankingservice.api;

import com.clientes.cuentas.bankingservice.BaseIntegrationTest;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.CustomerAccountDTO;
import com.clientes.cuentas.bankingservice.infrastructure.input.dto.CustomerDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

  @Test
  @DisplayName("1. Comprueba que devuelve los 5 clientes iniciales.")
  void shouldReturnAllCustomers() throws Exception {
    mockMvc.perform(get("/clientes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(5))
            .andExpect(jsonPath("$.totalElements").value(5));
  }

  @Test
  @DisplayName("2. Comprueba que devuelve solo los mayores de 18 años.")
  void shouldReturnOnlyAdultCustomers() throws Exception {
    MvcResult result = mockMvc.perform(get("/clientes/mayores-de-edad"))
            .andExpect(status().isOk())
            .andReturn();

    String json = result.getResponse().getContentAsString();
    var response = objectMapper.readTree(json);
    List<CustomerDTO> customers = objectMapper.convertValue(response.get("content"), new TypeReference<>() {});

    LocalDate adultLimit = LocalDate.now().minusYears(18);
    customers.forEach(customer -> assertFalse(customer.getBirthDate().isAfter(adultLimit)));
  }

  @Test
  @DisplayName("3. Comprueba que filtra correctamente")
  void shouldReturnFourCustomersWhenMinBalanceIs300()  throws Exception {
    mockMvc.perform(get("/clientes/con-cuenta-superior-a/{cantidad}", new BigDecimal("300.00")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(4))
            .andExpect(jsonPath("$.totalElements").value(4));
  }

  @Test
  @DisplayName("4. Comprueba que devuelve correctamente el cliente solicitado por su DNI.")
  void shouldReturnCustomerByDni() throws Exception {
    // Cliente '22222222B' (Raúl Canales Rodríguez) tiene 2 cuentas en BD: NRML y JR
    MvcResult result = mockMvc.perform(get("/clientes/{dni}", "22222222B"))
            .andExpect(status().isOk())
            .andReturn();

    // Se lee explícitamente como UTF-8 para evitar la decodificación incorrecta
    // por defecto (ISO-8859-1) que corrompería los caracteres acentuados
    String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    CustomerAccountDTO customer = objectMapper.readValue(json, CustomerAccountDTO.class);

    assertEquals("22222222B", customer.getDni());
    assertEquals("Raúl", customer.getName());
    assertEquals("Canales", customer.getSurname1());
    assertEquals("Rodríguez", customer.getSurname2());
    assertEquals(LocalDate.of(1985, 3, 1), customer.getBirthDate());
    assertEquals(2, customer.getAccounts().size());
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
