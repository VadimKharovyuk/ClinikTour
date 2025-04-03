package com.example.cliniktour.dto.blog;

import com.example.cliniktour.enums.BlogPostType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//* DTO для детального отображения блог-поста
// */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostDetailDto {

    private Long id;
    private String title;
    private String content;
    private String excerpt;
    private String imagePath;
    private BlogPostType postType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
