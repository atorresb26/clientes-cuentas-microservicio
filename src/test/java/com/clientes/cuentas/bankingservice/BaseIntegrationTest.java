package com.clientes.cuentas.bankingservice;

import com.clientes.cuentas.bankingservice.infrastructure.config.TestSecurityConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for all integration tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
@Import(TestSecurityConfig.class)
public abstract class BaseIntegrationTest {
}
