package com.clientes.cuentas.bankingservice.infrastructure.persistence.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Projection to obtain the results of the SQL query that returns all customers
 * with their associated accounts and avoid N+1 queries.
 *
 * @param id the customer id
 * @param dni the customer dni
 * @param name the customer name
 * @param surname1 the customer surname1
 * @param surname2 the customer surname2
 * @param birthDate the customer birthdate
 * @param bankAccountId the related bank account id
 * @param bankAccountApiId the related bank account api id
 * @param bankAccountType the related bank account type
 * @param total the total of account
 */
public record CustomerAccountRow(
        Long id,
        String dni,
        String name,
        String surname1,
        String surname2,
        LocalDate birthDate,
        Long bankAccountId,
        String bankAccountApiId,
        String bankAccountType,
        BigDecimal total
) {
}
