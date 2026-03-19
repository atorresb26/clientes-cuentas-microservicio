package com.clientes.cuentas.bankingservice.application.port;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.clientes.cuentas.bankingservice.application.port.dto.PaginationRequestDTO;

/**
 * Utility class for converting pagination DTOs to Spring Data's Pageable.
 */
public class PaginationUtils {

  private PaginationUtils() {
    // Utility class
  }

  /**
   * Converts a PaginationRequestDTO to a Spring Data Pageable.
   *
   * @param pagination the pagination request DTO
   * @return a Pageable instance ready to use with Spring Data repositories
   */
  public static Pageable toPageable(PaginationRequestDTO pagination) {
    if (pagination == null) {
      pagination = new PaginationRequestDTO();
    }

    pagination = pagination.withDefaults();

    Sort sort = parseSortCriteria(pagination.getSort());
    return PageRequest.of(pagination.getPage(), pagination.getSize(), sort);
  }

  /**
   * Parses sort criteria from string format.
   * Expected format: "field,direction" or "field1,direction1;field2,direction2"
   * Example: "name,asc" or "name,asc;surname1,desc"
   *
   * @param sortString the sort criteria string
   * @return a Sort instance, or Sort.unsorted() if no valid criteria is provided
   */
  private static Sort parseSortCriteria(String sortString) {
    if (sortString == null || sortString.trim().isEmpty()) {
      return Sort.unsorted();
    }

    String[] sortCriteria = sortString.split(";");
    Sort sort = Sort.unsorted();

    for (String criterion : sortCriteria) {
      String[] parts = criterion.trim().split(",");
      if (parts.length == 2) {
        String field = parts[0].trim();
        String direction = parts[1].trim().toLowerCase();

        Sort.Direction dir = "desc".equals(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        sort = sort.and(Sort.by(dir, field));
      }
    }

    return sort;
  }
}

