package com.clientes.cuentas.demo.infrastructure.persistence.repository;

import com.clientes.cuentas.demo.infrastructure.persistence.entity.CustomerEntity;
import com.clientes.cuentas.demo.infrastructure.persistence.projection.CustomerAccountRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaCustomerRepository extends JpaRepository<CustomerEntity, Long> {

  @Query("""
          SELECT new com.clientes.cuentas.demo.infrastructure.persistence.projection.CustomerAccountRow(
                      c.id,
                      c.dni,
                      c.name,
                      c.surname1,
                      c.surname2,
                      c.birthDate,
                      ba.id AS bankAccountId,
                      ba.accountType.name AS bankAccountType,
                      ba.total
                      )
            FROM CustomerEntity c
            LEFT JOIN BankAccountEntity ba
            ON ba.customer.id = c.id
          """)
  List<CustomerAccountRow> getCustomersAndAccounts();
}

