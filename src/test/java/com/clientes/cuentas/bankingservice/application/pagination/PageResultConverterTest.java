package com.clientes.cuentas.bankingservice.application.pagination;

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
}
