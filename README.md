# clientes-cuentas-microservicio

Microservicio Spring Boot para la gestión de clientes bancarios y sus cuentas, construido con enfoque de **arquitectura hexagonal**, contrato **OpenAPI-first**, seguridad con **JWT/Keycloak**, persistencia con **Spring Data JPA + H2**, caché con **Caffeine**, métricas con **Micrometer/Actuator** y una batería de tests que cubre negocio, integración y arquitectura.

## 1. Descripción completa del proyecto

Este proyecto expone una API REST para consultar clientes, filtrar clientes por criterios de negocio y operar sobre cuentas bancarias.

### Qué hace funcionalmente

Permite:

- Obtener todos los clientes junto con sus cuentas.
- Obtener solo los clientes mayores de edad.
- Filtrar clientes cuya suma total de cuentas supere una cantidad.
- Consultar el detalle de un cliente por DNI.
- Crear una nueva cuenta bancaria para un cliente existente.
- Crear una nueva cuenta bancaria para un cliente inexistente.
- Consultar una cuenta bancaria por su `apiId`.
- Actualizar el saldo total de una cuenta.

### Enfoque arquitectónico

La solución está organizada alrededor de una **arquitectura hexagonal** y el propio proyecto incluye tests de arquitectura con **ArchUnit** para vigilar que se mantenga esa separación.

Capas principales:

- **`domain`**
  - Modelos puros (`Customer`, `BankAccount`)
  - Value Objects (`Dni`, `Money`)
  - Enumeraciones y excepciones de negocio
  - Sin dependencias de persistencia ni de framework

- **`application`**
  - Casos de uso (`usecase`)
  - Servicios de aplicación (`service`)
  - Puertos/repositorios (`application.repository`)
  - Mappers y utilidades de paginación
  - Orquesta negocio sin conocer detalles técnicos concretos

- **`infrastructure`**
  - **Entrada API**: delegates REST que implementan el contrato OpenAPI generado
  - **Persistencia**: entidades JPA, repositorios Spring Data, adapters y mappers
  - **Seguridad**: configuración JWT y gestión uniforme de errores 401/403
  - **Configuración**: exposición estática de la especificación OpenAPI

### Buenas prácticas y decisiones técnicas que incorpora

- **OpenAPI-first**: el contrato vive en `src/main/resources/openapi/api-definition.yaml`.
- **Generación de código** con OpenAPI Generator:
  - interfaces API
  - DTOs
  - patrón **delegate**
- **Separación estricta de capas** validada con **ArchUnit**.
- **Validación de entrada** con Bean Validation y validaciones de dominio adicionales.
- **Errores estandarizados** con `ProblemDetail` (RFC 7807).
- **Seguridad basada en roles** con Keycloak como proveedor OAuth2/OpenID Connect.
- **Conversión de roles del token JWT** a autoridades Spring (`ROLE_admin`, `ROLE_user`).
- **Caché** para lecturas frecuentes:
  - cliente por DNI con cuentas
  - cuenta por `apiId`
  - tipo de cuenta por código
- **Métricas** con `@Timed` y exposición por Actuator.
- **Tests de varios niveles**:
  - unitarios
  - integración de API
  - integración de persistencia
  - tests de arquitectura
- **Cobertura** con JaCoCo.

### Stack tecnológico

- Java 21
- Spring Boot 3.5.11
- Spring Web
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server (JWT)
- Spring Validation
- H2 Database
- Springdoc OpenAPI UI
- OpenAPI Generator
- MapStruct
- Micrometer + Actuator
- Caffeine Cache
- JUnit 5
- Mockito
- ArchUnit
- JaCoCo
- Docker / Docker Compose
- Keycloak

## 2. Estructura del proyecto

Estructura conceptual:

```text
src/main/java/com/clientes/cuentas/bankingservice
├── domain
│   ├── enums
│   ├── exception
│   └── model
├── application
│   ├── command
│   ├── constants
│   ├── mapper
│   ├── pagination
│   ├── repository
│   ├── service
│   └── usecase
├── infrastructure
│   ├── api
│   │   ├── delegate
│   │   ├── exception
│   │   └── mapper
│   ├── config
│   ├── persistence
│   │   ├── entity
│   │   ├── mapper
│   │   ├── projection
│   │   └── repository
│   └── security
└── config
```

Además:

- `src/main/resources/openapi/`: contrato OpenAPI y componentes reutilizables.
- `src/main/resources/db/`: DDL y datos semilla.
- `keycloak/banking-realm.json`: realm exportado con cliente, roles y usuarios de prueba.

## 3. Seguridad y autenticación

La aplicación protege todos los endpoints y después aplica autorización por método con `@PreAuthorize`.

### Roles existentes en Keycloak

- `admin`: acceso a las operaciones de la API
- `user`: autenticado, pero **sin permisos** para los endpoints funcionales documentados actualmente

### Usuarios de prueba reales incluidos en el realm

> Nota importante: en el export de Keycloak los usuarios están en **camelCase**. Es decir, las credenciales válidas son estas:

- `adminUser` / `admin`
- `readUser` / `read`

### Cliente OAuth2/OpenID Connect

- `clientId`: `banking-service`
- Realm: `banking`
- Direct Access Grants: habilitado
- Cliente público: sí

### Endpoint para obtener token

Desde host local:

- `http://localhost:8081/realms/banking/protocol/openid-connect/token`

## 4. Datos semilla incluidos

Cuando la aplicación arranca con la configuración por defecto, inicializa una base H2 en memoria y carga:

### Tipos de cuenta

- `JR` → `JUNIOR`
- `NRML` → `NORMAL`
- `PREM` → `PREMIUM`

### Clientes iniciales

| DNI | Nombre | Fecha nacimiento | Observación |
|---|---|---|---|
| `11111111A` | Juan Pérez López | `1959-09-12` | Tiene 2 cuentas |
| `22222222B` | Raúl Canales Rodríguez | `1985-03-01` | Tiene 2 cuentas |
| `33333333C` | Elena Ruiz Herrera | `2010-05-10` | Menor de edad |
| `44444444D` | Raquel Ruiz Herrera | `2002-06-21` | Mayor de edad |
| `55555555E` | María Sánchez Torres | `1999-08-08` | Mayor de edad |

### Totales útiles para pruebas manuales

| DNI | Suma aproximada de cuentas |
|---|---:|
| `11111111A` | `170000.00` |
| `22222222B` | `50300.00` |
| `33333333C` | `300.00` |
| `44444444D` | `75000.00` |
| `55555555E` | `120000.00` |

Esto hace especialmente útiles estas pruebas:

- `/clientes/con-cuenta-superior-a/300` → 4 clientes
- `/clientes/con-cuenta-superior-a/60000` → 3 clientes
- `/clientes/con-cuenta-superior-a/130000` → 1 cliente

## 5. Endpoints disponibles

> Todos los endpoints funcionales requieren token JWT y, en la práctica actual, rol `admin`.

### Clientes

| Método | Ruta | Descripción | Auth | Observaciones |
|---|---|---|---|---|
| `GET` | `/clientes` | Lista paginada de clientes con sus cuentas | JWT admin | Soporta `page`, `size`, `sort` |
| `GET` | `/clientes/mayores-de-edad` | Lista paginada de clientes mayores de edad | JWT admin | Soporta `page`, `size`, `sort` |
| `GET` | `/clientes/con-cuenta-superior-a/{cantidad}` | Lista paginada de clientes cuya suma de cuentas supera la cantidad | JWT admin | `cantidad >= 0` |
| `GET` | `/clientes/{dni}` | Detalle de un cliente con sus cuentas | JWT admin | DNI con patrón `^\d{8}[A-Za-z]$` |

### Cuentas

| Método | Ruta | Descripción | Auth | Observaciones |
|---|---|---|---|---|
| `POST` | `/cuentas` | Crea una cuenta bancaria y la asocia al cliente | JWT admin | Si el cliente no existe, se crea con el DNI indicado |
| `GET` | `/cuentas/{apiIdCuenta}` | Obtiene el detalle de una cuenta | JWT admin | `apiIdCuenta` es UUID |
| `PATCH` | `/cuentas/{apiIdCuenta}` | Actualiza el saldo total de una cuenta | JWT admin | Body con `total >= 0` |

### Comportamientos relevantes a conocer

- Si se crea una cuenta para un cliente inexistente, el servicio crea primero el cliente usando el DNI recibido.
- El contrato de creación **no pide nombre, apellidos ni fecha de nacimiento**, por lo que esos campos pueden quedar `null` si el cliente se crea en ese momento.
- Los errores se devuelven como `application/problem+json`.

## 6. Ejemplos de payloads

### `POST /cuentas`

```json
{
  "dniCliente": "11111111A",
  "codTipoCuenta": "JR",
  "total": 500.00
}
```

### `PATCH /cuentas/{apiIdCuenta}`

```json
{
  "total": 2500.00
}
```

## 7. Cómo ejecutar el proyecto

## Requisitos previos

- Java 21
- Docker y Docker Compose
- Opcional: Postman

## Ejecución recomendada con Docker Compose

Esta es la forma más cómoda porque la aplicación ya queda conectada con Keycloak usando el hostname interno `keycloak`.

### 1) Construir el JAR

```powershell
.\mvnw.cmd clean package
```

### 2) Levantar el entorno

```powershell
docker compose up --build
```

### Servicios resultantes

- API: `http://localhost:8080`
- Keycloak: `http://localhost:8081`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI YAML estático: `http://localhost:8080/openapi/api-definition.yaml`
- H2 Console: `http://localhost:8080/h2-console`
- Actuator: `http://localhost:8080/actuator`
- Consola de administración de Keycloak: `http://localhost:8081/admin`

### Credenciales útiles

**Keycloak Admin Console**

- usuario: `admin`
- contraseña: `admin`

**Usuarios de la API**

- `adminUser` / `admin`
- `readUser` / `read`

## 8. Cómo ejecutar los tests

La suite de tests se ha verificado en este proyecto con:

```powershell
.\mvnw.cmd test
```

### Qué cubren los tests

- lógica de dominio
- casos de uso
- mappers
- paginación
- integración API
- integración de persistencia
- manejo de excepciones
- reglas de arquitectura hexagonal con ArchUnit

### Detalle importante sobre seguridad en tests

Los tests de integración importan `TestSecurityConfig`, que permite todas las peticiones, por lo que **no requieren un Keycloak levantado** para ejecutarse.

### Informe de cobertura

Tras ejecutar tests, JaCoCo genera el informe en:

- `target/site/jacoco/index.html`

## 9. Postman

Se incluye una colección importable en la raíz del proyecto:

- `clientes-cuentas-microservicio.postman_collection.json`

### Qué contiene

#### Autenticación

- Obtener token JWT de `adminUser/admin`
- Obtener token JWT de `readUser/read`

#### Clientes

- `GET /clientes`
- `GET /clientes/mayores-de-edad`
- `GET /clientes/con-cuenta-superior-a/300`
- `GET /clientes/con-cuenta-superior-a/60000`
- `GET /clientes/con-cuenta-superior-a/130000`
- `GET /clientes/{dni}` con varios DNIs

#### Cuentas

- `POST /cuentas` para cliente existente
- `POST /cuentas` para cliente nuevo
- `PATCH /cuentas/{apiIdCuenta}`
- `GET /cuentas/{apiIdCuenta}`

#### Casos recomendables adicionales

- `GET /clientes` sin token → `401`
- `GET /clientes` con `readUser` → `403`
- `GET /clientes/{dni}` con DNI inválido → `400`
- `GET /cuentas/{apiId}` inexistente → `404`
- `POST /cuentas` con body inválido → `400`

### Orden recomendado de ejecución en Postman

1. Obtener token admin
2. Obtener token read
3. Ejecutar consultas de clientes
4. Ejecutar `POST /cuentas` para cliente existente o nuevo
5. Ejecutar `PATCH /cuentas/{{createdAccountApiId}}`
6. Ejecutar `GET /cuentas/{{createdAccountApiId}}`
7. Ejecutar negativos (`401`, `403`, `400`, `404`)

### Variables de la colección

La colección ya define variables útiles como:

- `baseUrl`
- `keycloakUrl`
- `realm`
- `clientId`
- `adminUsername`
- `adminPassword`
- `readUsername`
- `readPassword`
- `accessToken`
- `readAccessToken`
- `createdAccountApiId`
- `newCustomerDni`

## 10. Observabilidad y utilidades

### Swagger / OpenAPI

- UI: `http://localhost:8080/swagger-ui/index.html`
- Contrato: `http://localhost:8080/openapi/api-definition.yaml`

### Actuator

Se exponen:

- `health`
- `info`
- `metrics`
- `caches`

Ruta base:

- `http://localhost:8080/actuator`

### H2 Console

Disponible en:

- `http://localhost:8080/h2-console`

Parámetros típicos:

- JDBC URL: `jdbc:h2:mem:testdb`
- usuario: `sa`
- contraseña: vacía

## 11. Notas finales

- La seguridad real está implementada en código, aunque el contrato OpenAPI no modela explícitamente el esquema de seguridad con `securitySchemes`.
- La documentación Swagger UI carga el YAML estático del proyecto, no una especificación generada dinámicamente desde anotaciones.
