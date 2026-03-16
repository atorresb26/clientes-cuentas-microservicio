package com.clientes.cuentas.demo;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for all integration tests.
 *
 * <p>All subclasses share the same Spring application context thanks to
 * Spring Test's context caching mechanism. This guarantees that the
 * SQL initialization scripts ({@code spring.sql.init.mode=always}) are
 * executed only once, avoiding "table already exists" errors when
 * multiple test classes are run together (e.g. via Maven Surefire).</p>
 *
 * <p>{@code @Transactional} + {@code @Rollback} ensure that any data
 * written during a test is rolled back after each test method, keeping
 * tests isolated from each other without reloading the context.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public abstract class BaseIntegrationTest {
}

