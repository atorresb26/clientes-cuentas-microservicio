package com.clientes.cuentas.bankingservice.application.port;

import com.clientes.cuentas.bankingservice.application.port.dto.PaginationResponseDTO;
import com.clientes.cuentas.bankingservice.application.port.model.PageResult;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageResultConverterTest {

  @Test
  void shouldConvertSpringPageToPageResult() {
    List<String> content = List.of("one", "two");
    var page = new PageImpl<>(content, PageRequest.of(1, 2), 5);

    PageResult<String> result = PageResultConverter.fromPage(page);

    assertEquals(content, result.getContent());
    assertEquals(1, result.getPageNumber());
    assertEquals(2, result.getPageSize());
    assertEquals(5, result.getTotalElements());
    assertEquals(3, result.getTotalPages());
    assertFalse(result.isFirst());
    assertFalse(result.isLast());
    assertTrue(result.hasNext());
    assertTrue(result.hasPrevious());
  }

  @Test
  void shouldConvertPageResultToResponseDto() {
    PageResult<String> pageResult = PageResult.<String>builder()
            .content(List.of("one"))
            .pageNumber(0)
            .pageSize(10)
            .totalElements(1)
            .totalPages(1)
            .isFirst(true)
            .isLast(true)
            .build();

    PaginationResponseDTO<String> response = PageResultConverter.toResponseDTO(pageResult);

    assertEquals(pageResult.getContent(), response.getContent());
    assertEquals(pageResult.getPageNumber(), response.getCurrentPage());
    assertEquals(pageResult.getPageSize(), response.getPageSize());
    assertEquals(pageResult.getTotalElements(), response.getTotalElements());
    assertEquals(pageResult.getTotalPages(), response.getTotalPages());
    assertTrue(response.getIsFirst());
    assertTrue(response.getIsLast());
    assertFalse(response.getHasNext());
    assertFalse(response.getHasPrevious());
  }
}

