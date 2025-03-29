package com.example.cliniktour.dto.blog;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
