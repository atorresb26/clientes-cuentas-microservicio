package com.clientes.cuentas.bankingservice.application.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic wrapper for paginated data results.
 *
 * @param <T> the type of elements in the page
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResult<T> {

  private List<T> content;
  private int pageNumber;
  private int pageSize;
  private long totalElements;
  private int totalPages;
  private boolean isFirst;
  private boolean isLast;

  public boolean hasNext() {
    return !isLast;
  }

  public boolean hasPrevious() {
    return !isFirst;
  }
}

