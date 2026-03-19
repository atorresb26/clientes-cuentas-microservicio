package com.clientes.cuentas.bankingservice.application.pagination;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

/**
 * Utility class for converting paginated models between layers.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageResultConverter {

  /**
   * Converts a Spring Data Page to a PageResult.
   *
   * @param page the Spring Data page
   * @param <T>  the type of content
   * @return a PageResult with pagination metadata
   */
  public static <T> PageResult<T> fromPage(Page<T> page) {
    return PageResult.<T>builder()
            .content(page.getContent())
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .isFirst(page.isFirst())
            .isLast(page.isLast())
            .build();
  }
}
