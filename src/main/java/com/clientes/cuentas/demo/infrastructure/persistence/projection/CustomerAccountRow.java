package com.clientes.cuentas.demo.infrastructure.persistence.projection;

import java.time.LocalDate;

public record CustomerAccountRow(
        Long id,
        String dni,
        String name,
        String surname1,
        String surname2,
        LocalDate birthDate,
        Long bankAccountId,
        String bankAccountType,
        Double total
) {
}
