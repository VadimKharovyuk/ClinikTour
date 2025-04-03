package com.example.cliniktour.dto.blog;

import com.example.cliniktour.enums.BlogPostType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO для пагинированного списка блог-постов
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostPageDto {

    private List<BlogPostListDto> posts;
    private int currentPage;
    private int totalPages;
    private long totalElements;
}