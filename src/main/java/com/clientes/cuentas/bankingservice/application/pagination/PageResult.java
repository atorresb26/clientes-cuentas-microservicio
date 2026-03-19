package com.clientes.cuentas.bankingservice.application.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic wrapper for paginated data at the application/domain layer.
 * This class represents paginated results independent of the persistence layer.
 *
 * @param <T> the type of elements in the page
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResult<T> {

  /**
   * The content of the current page.
   */
  private List<T> content;

  /**
   * Current page number (zero-indexed).
   */
  private int pageNumber;

  /**
   * Size of the page.
   */
  private int pageSize;

  /**
   * Total number of elements.
   */
  private long totalElements;

  /**
   * Total number of pages.
   */
  private int totalPages;

  /**
   * Whether this is the first page.
   */
  private boolean isFirst;

  /**
   * Whether this is the last page.
   */
  private boolean isLast;

  /**
   * Whether there is a next page.
   */
  public boolean hasNext() {
    return !isLast;
  }

  /**
   * Whether there is a previous page.
   */
  public boolean hasPrevious() {
    return !isFirst;
  }
}


