package com.clientes.cuentas.bankingservice.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Architecture tests to guarantee the integrity of the hexagonal architecture.
 * <p>
 * Validated rules:
 * - Domain NEVER depends on infrastructure or application
 * - Application NEVER depends on infrastructure (API)
 * - Presentation (API) NEVER depends on infrastructure.persistence
 * - Absence of cycles between layers
 */
@AnalyzeClasses(packages = "com.clientes.cuentas.bankingservice")
public class HexagonalArchitectureTest {

  // ===========================================
  // RULE 1: Domain - Complete Isolation
  // ===========================================
  /**
   * Domain CANNOT depend on application or infrastructure.
   * Domain is the heart, self-contained and without external dependencies.
   * <p>
   * Violation: If domain imports @Entity (JPA), @Service, @Repository, etc.
   */
  @ArchTest
  public static final ArchRule domain_should_not_depend_on_application =
          noClasses()
                  .that().resideInAPackage("..domain..")
                  .should().dependOnClassesThat()
                  .resideInAPackage("..application..")
                  .because("Domain is self-contained and should not know the use cases");

  @ArchTest
  public static final ArchRule domain_should_not_depend_on_infrastructure =
          noClasses()
                  .that().resideInAPackage("..domain..")
                  .should().dependOnClassesThat()
                  .resideInAnyPackage(
                          "..infrastructure..",
                          "jakarta.persistence..",
                          "org.springframework.data..",
                          "org.hibernate..",
                          "com.fasterxml.jackson.."
                  )
                  .because("Domain should not know persistence details, external APIs, or frameworks");

  // ===========================================
  // RULE 2: Application - Dependency Control
  // ===========================================
  /**
   * Application can depend on domain, but NOT on infrastructure.persistence or infrastructure.api.
   * Application contains use cases and orchestration, NOT technical details.
   */
  @ArchTest
  public static final ArchRule application_should_not_depend_on_infrastructure_persistence =
          noClasses()
                  .that().resideInAPackage("..application..")
                  .should().dependOnClassesThat()
                  .resideInAPackage("..infrastructure.persistence..")
                  .because("Application uses ports (interfaces), not persistence implementations");

  @ArchTest
  public static final ArchRule application_should_not_depend_on_infrastructure_api =
          noClasses()
                  .that().resideInAPackage("..application..")
                  .should().dependOnClassesThat()
                  .resideInAPackage("..infrastructure.api..")
                  .because("Application must not know about controller details or input DTOs");

  /**
   * Application can depend on infrastructure.config and infrastructure.output
   * (output ports, general configuration, etc.) but not on JPA details.
   * application.port contains cross-layer utilities (DTOs, models) shared across layers.
   * These classes may have OpenAPI/Swagger annotations for API documentation.
   */
  @ArchTest
  public static final ArchRule application_should_only_use_infrastructure_ports =
          classes()
                  .that().resideInAPackage("..application..")
                  .should().onlyDependOnClassesThat()
                  .resideInAnyPackage(
                          "..application..",
                          "..domain..",
                          "..infrastructure.config..",
                          "..infrastructure.output..",
                          "..infrastructure.port..",
                          "jakarta..",
                          "io.swagger.v3..",
                          "org.springframework..",
                          "org.slf4j..",
                          "org.mapstruct..",
                          "org.junit..",
                          "org.mockito..",
                          "com.fasterxml.jackson..",
                          "java..",
                          "lombok.."
                  )
                  .because("Application only consumes interfaces (ports) and configurations, not implementations");

  // ===========================================
  // RULE 3: Presentation (API) - Input Isolation
  // ===========================================
  /**
   * API CANNOT depend on infrastructure.persistence (persistence).
   * API is the input gateway that injects into application.
   */
  @ArchTest
  public static final ArchRule api_should_not_depend_on_infrastructure_persistence =
          noClasses()
                  .that().resideInAPackage("..api..")
                  .should().dependOnClassesThat()
                  .resideInAPackage("..infrastructure.persistence..")
                  .because("API should not know JPA entities or database details");

  /**
   * API can depend on application and domain, but NOT on infrastructure.persistence.entity
   * The DTOs generated by OpenAPI can reside in infrastructure.input.dto, which is allowed.
   */
  @ArchTest
  public static final ArchRule api_should_only_use_application_and_domain =
          classes()
                  .that().resideInAPackage("..infrastructure.input.api..")
                  .should().onlyDependOnClassesThat()
                  .resideInAnyPackage(
                          "..infrastructure.input..",
                          "..infrastructure.config..",
                          "..application..",
                          "..domain..",
                          "io.swagger.v3..",
                          "jakarta..",
                          "org.springframework..",
                          "org.slf4j..",
                          "com.fasterxml.jackson..",
                          "java..",
                          "lombok.."
                  )
                  .because("API is entry point, orchestrates application and exposes domain");

  // ===========================================
  // RULE 4: Persistence - Implementation, not Orchestration
  // ===========================================
  /**
   * Persistence MUST NOT contain business logic (application service).
   * Persistence ONLY implements ports and accesses the database.
   * Exception: Integration tests that test the integration between services and persistence.
   */
  @ArchTest
  public static final ArchRule persistence_should_not_import_service_layer =
          noClasses()
                  .that().resideInAPackage("..infrastructure.persistence..")
                  .and().doNotHaveSimpleName("CreateBankAccountForCustomerServiceIntegrationTest")
                  .should().dependOnClassesThat()
                  .resideInAPackage("..application.service..")
                  .because("Persistence is output adapter, implements ports, does not use them");

  // ===========================================
  // RULE 5: Configuration - Allowed Centralization
  // ===========================================
  /**
   * Config can depend on everything (it is the wiring), but nothing else should depend on config.
   */
  @ArchTest
  public static final ArchRule no_classes_except_main_should_depend_on_config =
          noClasses()
                  .that().resideInAPackage("..application..")
                  .or().resideInAPackage("..domain..")
                  .or().resideInAPackage("..infrastructure.persistence..")
                  .or().resideInAPackage("..infrastructure.output..")
                  .should().dependOnClassesThat()
                  .resideInAPackage("..infrastructure.config..")
                  .because("Config is only allowed in Application.class (main) for initialization");

  // ===========================================
  // RULE 6: No cycles between layers
  // ===========================================
  /**
   * Verifies there are no cyclic dependencies between main slices.
   * Prohibited example: domain → application → infrastructure → domain
   */
  @ArchTest
  public static final ArchRule architecture_should_be_free_of_cycles =
          slices()
                  .matching("com.clientes.cuentas.bankingservice.(*)..")
                  .should().beFreeOfCycles()
                  .because("Hexagonal architecture must be acyclic to maintain unidirectional flow");

  // ===========================================
  // RULE 7: Domain Entities - Without JPA Annotations
  // ===========================================
  /**
   * Classes in domain.model MUST NOT have JPA annotations @Entity, @Table, @Column.
   * The mapping must be in infrastructure.persistence.entity.
   */
  @ArchTest
  public static final ArchRule domain_model_should_not_use_jpa_annotations =
          noClasses()
                  .that().resideInAPackage("..domain.model..")
                  .should().dependOnClassesThat()
                  .resideInAPackage("jakarta.persistence..")
                  .because("Domain models are not JPA entities; they must be pure POJOs");

  // ===========================================
  // RULE 8: Application Services - Without @Repository Annotations
  // ===========================================
  /**
   * Application services MUST NOT have @Repository or extend Repository.
   * They must use ports (interfaces) injected.
   */
  @ArchTest
  public static final ArchRule application_service_should_use_ports_not_repositories =
          noClasses()
                  .that().resideInAPackage("..application.service..")
                  .should().dependOnClassesThat()
                  .resideInAPackage("..infrastructure.persistence.repository..")
                  .because("Application services use ports (interfaces), not concrete JPA dependencies");

  // ===========================================
  // RULE 9: Enumerations and Value Objects
  // ===========================================
  /**
   * Domain enums MUST NOT have JPA annotations.
   */
  @ArchTest
  public static final ArchRule domain_enums_should_not_use_jpa =
          noClasses()
                  .that().resideInAPackage("..domain.enums..")
                  .should().dependOnClassesThat()
                  .resideInAPackage("jakarta.persistence..")
                  .because("Domain enums are pure types, without persistence details");

  // ===========================================
  // RULE 10: Defined Layers
  // ===========================================
  /**
   * Restricts that classes only reside in authorized packages within each layer.
   * Exceptions are allowed for: main Application class, configurations, base test classes, and architecture tests.
   * Also allows inner/anonymous classes.
   * application.port is a special cross-layer package containing DTOs and models shared across layers.
   */
  @ArchTest
  public static final ArchRule classes_should_be_organized_in_layers =
          classes()
                  .that().resideInAPackage("com.clientes.cuentas.bankingservice..")
                  .and().doNotHaveSimpleName("Application")
                  .and().doNotHaveSimpleName("ApplicationTest")
                  .and().doNotHaveSimpleName("BaseIntegrationTest")
                  .and().doNotHaveSimpleName("HexagonalArchitectureTest")
                  .and().doNotHaveSimpleName("CustomersApiIntegrationTest")
                  .and().doNotHaveSimpleName("BankAccountApiIntegrationTest")
                  .and().doNotHaveSimpleName("CacheConfig")
                  .and().doNotHaveSimpleName("MetricsConfig")
                  .and().haveNameNotMatching(".*\\$.*")
                  .should().resideInAnyPackage(
                          "com.clientes.cuentas.bankingservice.domain..",
                          "com.clientes.cuentas.bankingservice.application..",
                          "com.clientes.cuentas.bankingservice.infrastructure.."
                  )
                  .because("All classes must be in defined layers of the hexagonal architecture");
}
