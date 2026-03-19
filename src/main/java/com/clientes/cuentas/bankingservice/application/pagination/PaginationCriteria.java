package com.clientes.cuentas.bankingservice.application.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Application model for pagination parameters.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationCriteria {

  private Integer page;
  private Integer size;
  private String sort;

  /**
   * Applies default values when parameters are not provided.
   */
  public PaginationCriteria withDefaults() {
    if (Objects.isNull(this.page)) {
      this.page = 0;
    }
    if (Objects.isNull(this.size)) {
      this.size = 10;
    }
    return this;
  }
}
