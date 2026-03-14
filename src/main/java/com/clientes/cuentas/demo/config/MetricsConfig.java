package com.clientes.cuentas.demo.config;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class responsible for enabling method-level metrics
 * using Micrometer's {@link Timed} annotation.
 *
 * <p>This configuration registers a {@link TimedAspect}
 * bean that allows Spring AOP to intercept methods annotated with {@code @Timed}
 * and automatically record their execution time as metrics.</p>
 *
 * <p>The collected metrics are stored in the configured {@link MeterRegistry},
 * which acts as the central registry for all application metrics and can export
 * them to monitoring systems such as Prometheus, Datadog, or others supported
 * by Micrometer.</p>
 */
@Configuration
public class MetricsConfig {

  /**
   * Creates and registers the {@link TimedAspect} bean used to intercept
   * methods annotated with {@code @Timed}.
   *
   * <p>The aspect integrates with Spring AOP and records execution timing
   * metrics into the provided {@link MeterRegistry}.</p>
   *
   * @param meterRegistry the Micrometer registry responsible for storing
   *                      and publishing application metrics
   * @return a configured {@link TimedAspect} instance
   */
  @Bean
  public TimedAspect timedAspect(MeterRegistry meterRegistry) {
    return new TimedAspect(meterRegistry);
  }
}
