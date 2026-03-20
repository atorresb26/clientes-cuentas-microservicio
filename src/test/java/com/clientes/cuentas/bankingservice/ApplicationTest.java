package com.clientes.cuentas.bankingservice;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

/**
 * Unit tests for the {@link Application} main class.
 * Verifies that the Spring Boot application is bootstrapped correctly
 * by ensuring {@link SpringApplication#run(Class, String...)} is invoked
 * with the expected arguments.
 */
class ApplicationTest {

  @Test
  void mainShouldInvokeSpringApplicationRunWithCorrectArguments() {
    try (MockedStatic<SpringApplication> mockedStatic = mockStatic(SpringApplication.class)) {
      mockedStatic
              .when(() -> SpringApplication.run(Application.class, new String[]{}))
              .thenReturn(mock(ConfigurableApplicationContext.class));

      Application.main(new String[]{});

      mockedStatic.verify(() -> SpringApplication.run(Application.class, new String[]{}));
    }
  }
}
