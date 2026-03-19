package com.clientes.cuentas.bankingservice.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final SecurityProblemHandler securityProblemHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/public/**").permitAll()
                    .requestMatchers("/admin/**").hasRole("admin")
                    .requestMatchers("/user/**").hasAnyRole("user", "admin")
                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 ->
                    oauth2.jwt(jwt -> jwtAuthenticationConverter())
            )
            .exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint(securityProblemHandler)
                    .accessDeniedHandler(securityProblemHandler)
            );

    return http.build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

    converter.setJwtGrantedAuthoritiesConverter(jwt -> {
      Map<String, Object> realmAccess = jwt.getClaim("realm_access");
      if (Objects.isNull(realmAccess)) {
        return List.of();
      }

      Collection<String> roles = (Collection<String>) realmAccess.get("roles");

      return roles.stream()
              .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
              .map(GrantedAuthority.class::cast)
              .toList();
    });
    return converter;
  }
}
