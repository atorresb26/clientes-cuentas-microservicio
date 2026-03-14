package com.clientes.cuentas.demo.application.usecase;

import com.clientes.cuentas.demo.domain.model.Customer;

import java.util.List;

/**
 * Use case of obtaining all customers with higher amount than the received one.
 */
public interface GetCustomersWithHigherAmountUseCase {

  List<Customer> execute(Double amount);
}
