package com.clientes.cuentas.demo.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class responsible for enabling the application's
 * caching mechanism.
 *
 * <p>This class activates Spring's annotation-driven cache management
 * by using {@link EnableCaching}.
 */
@Configuration
@EnableCaching
public class CacheConfig {
  // Cache configuration class
}
