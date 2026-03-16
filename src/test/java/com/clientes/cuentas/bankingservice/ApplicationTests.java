package com.clientes.cuentas.bankingservice;

import org.junit.jupiter.api.Test;

/**
 * Integration tests for the Spring Boot application.
 *
 * <p>This test class verifies that the Spring application context
 * loads successfully. The {@code contextLoads()} test ensures that
 * the application configuration is valid and that all required
 * beans can be initialized without errors.</p>
 *
 * <p>Extends {@link BaseIntegrationTest} to share the same Spring context
 * with other integration tests and avoid SQL initialization conflicts.</p>
 */
class ApplicationTests extends BaseIntegrationTest {

  /**
   * Verifies that the Spring application context loads successfully.
   * The test will fail if the context cannot be started.
   */
  @Test
  void contextLoads() {

  }
}
