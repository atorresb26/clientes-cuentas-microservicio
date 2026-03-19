package com.clientes.cuentas.bankingservice.application.port;

import com.clientes.cuentas.bankingservice.application.port.dto.PaginationResponseDTO;
import com.clientes.cuentas.bankingservice.application.port.model.PageResult;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Utility class for converting paginated models between layers.
 */
public class PageResultConverter {

  private PageResultConverter() {
    // Utility class
  }

  /**
   * Converts a Spring Data Page to a PageResult.
   *
   * @param page the Spring Data page
   * @param <T> the type of content
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

  /**
   * Converts a PageResult to a PaginationResponseDTO.
   *
   * @param pageResult the PageResult from the application layer
   * @param <T> the type of content
   * @return a PaginationResponseDTO ready to be sent as API response
   */
  public static <T> PaginationResponseDTO<T> toResponseDTO(PageResult<T> pageResult) {
    return toResponseDTO(pageResult, pageResult.getContent());
  }

  /**
   * Converts a PageResult to a PaginationResponseDTO preserving pagination metadata
   * and replacing the content with an already mapped collection.
   *
   * @param pageResult the PageResult containing the pagination metadata
   * @param content the mapped content to include in the response
   * @param <S> the source content type
   * @param <T> the target content type
   * @return a PaginationResponseDTO ready to be adapted as API response
   */
  public static <S, T> PaginationResponseDTO<T> toResponseDTO(PageResult<S> pageResult, List<T> content) {
    return PaginationResponseDTO.<T>builder()
        .content(content)
        .currentPage(pageResult.getPageNumber())
        .pageSize(pageResult.getPageSize())
        .totalElements(pageResult.getTotalElements())
        .totalPages(pageResult.getTotalPages())
        .isFirst(pageResult.isFirst())
        .isLast(pageResult.isLast())
        .hasNext(pageResult.hasNext())
        .hasPrevious(pageResult.hasPrevious())
        .build();
  }
}

