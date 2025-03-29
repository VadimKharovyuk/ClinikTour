package com.example.cliniktour.dto.blog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для создания или обновления блог-поста
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostCreateDto {

    @NotBlank(message = "Заголовок статьи обязателен")
    @Size(max = 255, message = "Заголовок не может быть длиннее 255 символов")
    private String title;

    @NotBlank(message = "Содержание статьи обязательно")
    private String content;

    @Size(max = 500, message = "Краткое описание не может быть длиннее 500 символов")
    private String excerpt;

    private String imagePath;
}
