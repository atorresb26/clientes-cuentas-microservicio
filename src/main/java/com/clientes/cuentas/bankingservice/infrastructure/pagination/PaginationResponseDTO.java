package com.clientes.cuentas.bankingservice.infrastructure.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic DTO for paginated responses.
 * Wraps paginated content with metadata about the pagination.
 *
 * @param <T> the type of content in the page
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationResponseDTO<T> {

  /**
   * The actual content of the page.
   */
  @JsonProperty("content")
  @Schema(description = "The content of the current page")
  private List<T> content;

  /**
   * Current page number (zero-indexed).
   */
  @JsonProperty("currentPage")
  @Schema(description = "Current page number (zero-indexed)", example = "0")
  private Integer currentPage;

  /**
   * Size of the current page.
   */
  @JsonProperty("pageSize")
  @Schema(description = "Number of records in the current page", example = "20")
  private Integer pageSize;

  /**
   * Total number of elements across all pages.
   */
  @JsonProperty("totalElements")
  @Schema(description = "Total number of elements across all pages", example = "100")
  private Long totalElements;

  /**
   * Total number of pages.
   */
  @JsonProperty("totalPages")
  @Schema(description = "Total number of pages", example = "5")
  private Integer totalPages;

  /**
   * Whether this is the first page.
   */
  @JsonProperty("isFirst")
  @Schema(description = "Whether this is the first page", example = "true")
  private Boolean isFirst;

  /**
   * Whether this is the last page.
   */
  @JsonProperty("isLast")
  @Schema(description = "Whether this is the last page", example = "false")
  private Boolean isLast;

  /**
   * Whether there is a next page.
   */
  @JsonProperty("hasNext")
  @Schema(description = "Whether there is a next page", example = "true")
  private Boolean hasNext;

  /**
   * Whether there is a previous page.
   */
  @JsonProperty("hasPrevious")
  @Schema(description = "Whether there is a previous page", example = "false")
  private Boolean hasPrevious;
}



