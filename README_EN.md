# clientes-cuentas-microservicio

Spring Boot microservice for managing banking customers and their accounts, built with a **hexagonal architecture** approach, an **OpenAPI-first** contract, **JWT/Keycloak** security, **Spring Data JPA + H2** persistence, **Caffeine** cache, **Micrometer/Actuator** metrics, and a test suite that covers business logic, integration, and architecture.

## 1. Full project description

This project exposes a REST API to query customers, filter customers by business criteria, and operate on bank accounts.

### Functional scope

It allows you to:

- Retrieve all customers together with their accounts.
- Retrieve only adult customers.
- Filter customers whose total balance across accounts is greater than a given amount.
- Retrieve customer details by DNI.
- Create a new bank account for an existing customer.
- Create a new bank account for a non-existing customer.
- Retrieve a bank account by its `apiId`.
- Update the total balance of an account.

### Architectural approach

The solution is organized around a **hexagonal architecture**, and the project includes architecture tests with **ArchUnit** to ensure that this separation is preserved.

Main layers:

- **`domain`**
  - Pure models (`Customer`, `BankAccount`)
  - Value Objects (`Dni`, `Money`)
  - Business enums and exceptions
  - No persistence or framework dependencies

- **`application`**
  - Use cases (`usecase`)
  - Application services (`service`)
  - Ports/repositories (`application.repository`)
  - Mappers and pagination utilities
  - Orchestrates business logic without knowing concrete technical details

- **`infrastructure`**
  - **API input**: REST delegates implementing the generated OpenAPI contract
  - **Persistence**: JPA entities, Spring Data repositories, adapters, and mappers
  - **Security**: JWT configuration and consistent 401/403 error handling
  - **Configuration**: static serving of the OpenAPI specification

### Best practices and technical decisions included

- **OpenAPI-first**: the contract lives in `src/main/resources/openapi/api-definition.yaml`.
- **Code generation** with OpenAPI Generator:
  - API interfaces
  - DTOs
  - **delegate** pattern
- **Strict layer separation** validated with **ArchUnit**.
- **Input validation** with Bean Validation and additional domain validations.
- **Standardized errors** with `ProblemDetail` (RFC 7807).
- **Role-based security** with Keycloak as OAuth2/OpenID Connect provider.
- **JWT role conversion** to Spring authorities (`ROLE_admin`, `ROLE_user`).
- **Cache** for frequent reads:
  - customer by DNI with accounts
  - account by `apiId`
  - account type by code
- **Metrics** with `@Timed` and Actuator exposure.
- **Multi-level tests**:
  - unit
  - API integration
  - persistence integration
  - architecture tests
- **Coverage** with JaCoCo.

### Technology stack

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

## 2. Project structure

Conceptual structure:

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

Also:

- `src/main/resources/openapi/`: OpenAPI contract and reusable components.
- `src/main/resources/db/`: DDL and seed data.
- `keycloak/banking-realm.json`: exported realm with client, roles, and test users.

## 3. Security and authentication

The application protects all endpoints and then applies method-level authorization with `@PreAuthorize`.

### Existing roles in Keycloak

- `admin`: access to API operations
- `user`: authenticated, but **without permissions** for the currently documented functional endpoints

### Real test users included in the realm

> Important note: in the Keycloak export, users are in **camelCase**. Valid credentials are:

- `adminUser` / `admin`
- `readUser` / `read`

### OAuth2/OpenID Connect client

- `clientId`: `banking-service`
- Realm: `banking`
- Direct Access Grants: enabled
- Public client: yes

### Token endpoint

From localhost:

- `http://localhost:8081/realms/banking/protocol/openid-connect/token`

## 4. Included seed data

When the application starts with default configuration, it initializes an in-memory H2 database and loads:

### Account types

- `JR` -> `JUNIOR`
- `NRML` -> `NORMAL`
- `PREM` -> `PREMIUM`

### Initial customers

| DNI | Name | Birth date | Notes |
|---|---|---|---|
| `11111111A` | Juan Perez Lopez | `1959-09-12` | Has 2 accounts |
| `22222222B` | Raul Canales Rodriguez | `1985-03-01` | Has 2 accounts |
| `33333333C` | Elena Ruiz Herrera | `2010-05-10` | Underage |
| `44444444D` | Raquel Ruiz Herrera | `2002-06-21` | Adult |
| `55555555E` | Maria Sanchez Torres | `1999-08-08` | Adult |

### Useful totals for manual testing

| DNI | Approximate sum of accounts |
|---|---:|
| `11111111A` | `170000.00` |
| `22222222B` | `50300.00` |
| `33333333C` | `300.00` |
| `44444444D` | `75000.00` |
| `55555555E` | `120000.00` |

This makes these checks especially useful:

- `/clientes/con-cuenta-superior-a/300` -> 4 customers
- `/clientes/con-cuenta-superior-a/60000` -> 3 customers
- `/clientes/con-cuenta-superior-a/130000` -> 1 customer

## 5. Available endpoints

> All functional endpoints require a JWT token and, in current practice, the `admin` role.

### Customers

| Method | Path | Description | Auth | Notes |
|---|---|---|---|---|
| `GET` | `/clientes` | Paginated list of customers with their accounts | JWT admin | Supports `page`, `size`, `sort` |
| `GET` | `/clientes/mayores-de-edad` | Paginated list of adult customers | JWT admin | Supports `page`, `size`, `sort` |
| `GET` | `/clientes/con-cuenta-superior-a/{cantidad}` | Paginated list of customers whose account sum is above the amount | JWT admin | `cantidad >= 0` |
| `GET` | `/clientes/{dni}` | Customer detail with accounts | JWT admin | DNI pattern `^\d{8}[A-Za-z]$` |

### Accounts

| Method | Path | Description | Auth | Notes |
|---|---|---|---|---|
| `POST` | `/cuentas` | Creates a bank account and associates it with the customer | JWT admin | If customer does not exist, it is created with the provided DNI |
| `GET` | `/cuentas/{apiIdCuenta}` | Retrieves account detail | JWT admin | `apiIdCuenta` is UUID |
| `PATCH` | `/cuentas/{apiIdCuenta}` | Updates account total balance | JWT admin | Body with `total >= 0` |

### Relevant behavior details

- If an account is created for a non-existing customer, the service first creates the customer using the provided DNI.
- The create contract **does not require first name, last names, or birth date**, so those fields may be `null` when the customer is created in that flow.
- Errors are returned as `application/problem+json`.

## 6. Payload examples

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

## 7. How to run the project

## Prerequisites

- Java 21
- Docker and Docker Compose
- Optional: Postman

## Recommended run with Docker Compose

This is the easiest way because the application is already connected to Keycloak using the internal hostname `keycloak`.

### 1) Build the JAR

```powershell
.\mvnw.cmd clean package
```

### 2) Start the environment

```powershell
docker compose up --build
```

### Resulting services

- API: `http://localhost:8080`
- Keycloak: `http://localhost:8081`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Static OpenAPI YAML: `http://localhost:8080/openapi/api-definition.yaml`
- H2 Console: `http://localhost:8080/h2-console`
- Actuator: `http://localhost:8080/actuator`
- Keycloak Admin Console: `http://localhost:8081/admin`

### Useful credentials

**Keycloak Admin Console**

- username: `admin`
- password: `admin`

**API users**

- `adminUser` / `admin`
- `readUser` / `read`

## 8. How to run tests

The test suite has been verified in this project with:

```powershell
.\mvnw.cmd test
```

### What the tests cover

- domain logic
- use cases
- mappers
- pagination
- API integration
- persistence integration
- exception handling
- hexagonal architecture rules with ArchUnit

### Important security detail in tests

Integration tests import `TestSecurityConfig`, which allows all requests, so they **do not require Keycloak running** to execute.

### Coverage report

After running tests, JaCoCo generates the report at:

- `target/site/jacoco/index.html`

## 9. Postman

An importable collection is included in the `postman` folder:

- `clientes-cuentas-microservicio.postman_collection.json`

### What it contains

#### Authentication

- Get JWT token for `adminUser/admin`
- Get JWT token for `readUser/read`

#### Customers

- `GET /clientes`
- `GET /clientes/mayores-de-edad`
- `GET /clientes/con-cuenta-superior-a/300`
- `GET /clientes/con-cuenta-superior-a/60000`
- `GET /clientes/con-cuenta-superior-a/130000`
- `GET /clientes/{dni}` with multiple DNIs

#### Accounts

- `POST /cuentas` for existing customer
- `POST /cuentas` for new customer
- `PATCH /cuentas/{apiIdCuenta}`
- `GET /cuentas/{apiIdCuenta}`

#### Additional recommended cases

- `GET /clientes` without token -> `401`
- `GET /clientes` with `readUser` -> `403`
- `GET /clientes/{dni}` with invalid DNI -> `400`
- `GET /cuentas/{apiId}` not found -> `404`
- `POST /cuentas` with invalid body -> `400`

### Recommended Postman execution order

1. Get admin token
2. Get read token
3. Run customer queries
4. Run `POST /cuentas` for existing or new customer
5. Run `PATCH /cuentas/{{createdAccountApiId}}`
6. Run `GET /cuentas/{{createdAccountApiId}}`
7. Run negative cases (`401`, `403`, `400`, `404`)

### Collection variables

The collection already defines useful variables such as:

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

## 10. Observability and utilities

### Swagger / OpenAPI

- UI: `http://localhost:8080/swagger-ui/index.html`
- Contract: `http://localhost:8080/openapi/api-definition.yaml`

### Actuator

Exposed endpoints:

- `health`
- `info`
- `metrics`
- `caches`

Base path:

- `http://localhost:8080/actuator`

### H2 Console

Available at:

- `http://localhost:8080/h2-console`

Typical parameters:

- JDBC URL: `jdbc:h2:mem:testdb`
- username: `sa`
- password: empty

## 11. Final notes

- Real security is implemented in code, although the OpenAPI contract does not explicitly model the security scheme with `securitySchemes`.
- Swagger UI documentation loads the static project YAML, not a specification dynamically generated from annotations.

