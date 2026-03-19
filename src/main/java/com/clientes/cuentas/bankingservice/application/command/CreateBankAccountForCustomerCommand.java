package com.clientes.cuentas.bankingservice.application.command;

import java.math.BigDecimal;

/**
 * Command object used by the use case responsible for creating a bank account
 * for an existing customer.
 */
public record CreateBankAccountForCustomerCommand(
        String customerDni,
        String accountTypeCode,
        BigDecimal total
) {
}
