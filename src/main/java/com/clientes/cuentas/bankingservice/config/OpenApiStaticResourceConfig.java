package com.clientes.cuentas.bankingservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures the static resource handler for OpenAPI files served from the classpath.
 */
@Configuration
public class OpenApiStaticResourceConfig implements WebMvcConfigurer {

  /**
   * Registers the resource handler that serves OpenAPI static files from the configured classpath location.
   *
   * @param registry the registry used to add resource handler mappings
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
            .addResourceHandler("/openapi/**")
            .addResourceLocations("classpath:/openapi/");
  }
}
