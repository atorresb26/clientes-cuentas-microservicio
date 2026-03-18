package com.clientes.cuentas.bankingservice.api;

import com.clientes.cuentas.bankingservice.BaseIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BankAccountApiIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("4-1. Valida la creación de una nueva cuenta para un cliente existente")
  void shouldCreateBankAccountForExistingCustomer() throws Exception {
    mockMvc.perform(post("/cuentas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "dniCliente": "11111111A",
                              "codTipoCuenta": "JR",
                              "total": 500.00
                            }
                            """))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString("/cuentas/")))
            .andExpect(jsonPath("$.apiId", notNullValue()))
            .andExpect(jsonPath("$.accountType").value("JUNIOR"))
            .andExpect(jsonPath("$.total").value(500.0));
  }

  @Test
  @DisplayName("4-2. Valida la creación de una nueva cuenta para un cliente nuevo")
  void shouldCreateBankAccountForNewCustomer() throws Exception {
    mockMvc.perform(post("/cuentas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "dniCliente": "99999999Z",
                              "codTipoCuenta": "NRML",
                              "total": 200.00
                            }
                            """))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString("/cuentas/")))
            .andExpect(jsonPath("$.apiId", notNullValue()))
            .andExpect(jsonPath("$.accountType").value("NORMAL"))
            .andExpect(jsonPath("$.total").value(200.0));
  }

  @Test
  @DisplayName("5. Valida la actualización del saldo")
  void shouldUpdateBankAccountTotal() throws Exception {
    // Paso 1: Crear una cuenta para obtener un apiId válido y conocido
    MvcResult createResult = mockMvc.perform(post("/cuentas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "dniCliente": "22222222B",
                              "codTipoCuenta": "PREM",
                              "total": 1000.00
                            }
                            """))
            .andExpect(status().isCreated())
            .andReturn();

    String apiId = objectMapper.readTree(createResult.getResponse().getContentAsString())
            .get("apiId").asText();

    // Paso 2: Actualizar el saldo de la cuenta recién creada
    mockMvc.perform(patch("/cuentas/{apiId}", apiId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "total": 2500.00
                            }
                            """))
            .andExpect(status().isNoContent());

    // Paso 3: Verificar que el saldo fue actualizado correctamente
    mockMvc.perform(get("/cuentas/{apiId}", apiId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(2500.0));
  }
}
