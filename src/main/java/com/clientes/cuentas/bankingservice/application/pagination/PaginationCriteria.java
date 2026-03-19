package com.clientes.cuentas.bankingservice.application.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    if (this.page == null) {
      this.page = 0;
    }
    if (this.size == null) {
      this.size = 20;
    }
    return this;
  }
}

