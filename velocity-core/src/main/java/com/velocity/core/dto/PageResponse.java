package com.velocity.core.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Paginated response wrapper for list endpoints.
 * Provides pagination metadata along with the content.
 * 
 * @param <T> The type of content in the page
 * 
 * @author VeloCity Team
 * @version 1.0
 * @since 2026-02-01
 */
@Data
@Accessors(chain = true)
public class PageResponse<T> {
    
    /**
     * The list of items in the current page
     */
    private List<T> content;
    
    /**
     * Current page number (0-indexed)
     */
    private int pageNumber;
    
    /**
     * Number of items per page
     */
    private int pageSize;
    
    /**
     * Total number of items across all pages
     */
    private long totalElements;
    
    /**
     * Total number of pages
     */
    private int totalPages;
    
    /**
     * Whether this is the last page
     */
    private boolean last;
    
    /**
     * Whether this is the first page
     */
    private boolean first;
    
    /**
     * Whether the page is empty
     */
    private boolean empty;
    
    /**
     * Creates a PageResponse from Spring Data Page object
     * 
     * @param page Spring Data Page
     * @param <T> Type of content
     * @return PageResponse instance
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<T>()
                .setContent(page.getContent())
                .setPageNumber(page.getNumber())
                .setPageSize(page.getSize())
                .setTotalElements(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .setLast(page.isLast())
                .setFirst(page.isFirst())
                .setEmpty(page.isEmpty());
    }
    
    /**
     * Creates a PageResponse with custom content and pagination info
     * 
     * @param content List of items
     * @param pageNumber Current page number
     * @param pageSize Page size
     * @param totalElements Total elements
     * @param <T> Type of content
     * @return PageResponse instance
     */
    public static <T> PageResponse<T> of(
            List<T> content, 
            int pageNumber, 
            int pageSize, 
            long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        return new PageResponse<T>()
                .setContent(content)
                .setPageNumber(pageNumber)
                .setPageSize(pageSize)
                .setTotalElements(totalElements)
                .setTotalPages(totalPages)
                .setLast(pageNumber >= totalPages - 1)
                .setFirst(pageNumber == 0)
                .setEmpty(content.isEmpty());
    }
}
