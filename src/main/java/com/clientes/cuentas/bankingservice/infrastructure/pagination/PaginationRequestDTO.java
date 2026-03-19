package com.clientes.cuentas.bankingservice.infrastructure.pagination;

import com.clientes.cuentas.bankingservice.application.pagination.PaginationCriteria;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for pagination request parameters.
 * Provides reusable pagination parameters that can be used across different endpoints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationRequestDTO {

  /**
   * Page number (zero-indexed).
   */
  @Min(value = 0, message = "Page number must be greater than or equal to 0")
  @Schema(description = "Page number (zero-indexed)", example = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private Integer page;

  /**
   * Number of records per page.
   */
  @Min(value = 1, message = "Size must be greater than 0")
  @Schema(description = "Number of records per page", example = "20", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private Integer size;

  /**
   * Sort criteria in the format: 'field,direction' (e.g., 'name,asc' or 'surname1,desc').
   * Multiple sort criteria can be separated by semicolon (e.g., 'name,asc;dni,asc').
   */
  @Schema(description = "Sort criteria (format: field,direction)", example = "name,asc", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String sort;

  /**
   * Provides default values if not specified.
   * Default: page=0, size=20, no sorting.
   *
   * @return PaginationRequestDTO with defaults applied
   */
  public PaginationRequestDTO withDefaults() {
    if (this.page == null) {
      this.page = 0;
    }
    if (this.size == null) {
      this.size = 20;
    }
    return this;
  }

  /**
   * Converts this infrastructure DTO into an application pagination model.
   */
  public PaginationCriteria toCriteria() {
    PaginationRequestDTO pagination = this.withDefaults();
    return PaginationCriteria.builder()
        .page(pagination.getPage())
        .size(pagination.getSize())
        .sort(pagination.getSort())
        .build();
  }
}



