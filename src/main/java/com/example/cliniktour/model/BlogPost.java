package com.example.cliniktour.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "blog_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Заголовок статьи обязателен")
    @Size(max = 255, message = "Заголовок не может быть длиннее 255 символов")
    private String title;

    @NotBlank(message = "Содержание статьи обязательно")
    @Column(columnDefinition = "TEXT")
    private String content;

    @Size(max = 500, message = "Краткое описание не может быть длиннее 500 символов")
    @Column(columnDefinition = "TEXT")
    private String excerpt;

    // Путь к изображению
    private String imagePath;

    // Хеш для удаления изображения (если используется внешний сервис хранения)
    private String imageDeleteHash;


    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


}