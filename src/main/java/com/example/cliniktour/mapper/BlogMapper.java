package com.example.cliniktour.mapper;

import com.example.cliniktour.dto.blog.BlogPostCreateDto;
import com.example.cliniktour.dto.blog.BlogPostDetailDto;
import com.example.cliniktour.dto.blog.BlogPostListDto;
import com.example.cliniktour.model.BlogPost;
import com.example.cliniktour.util.ImgurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Маппер для преобразования между сущностями BlogPost и DTO
 */
@Component
@RequiredArgsConstructor
public class BlogMapper {

    private final ImgurService imgurService;

    /**
     * Конвертирует DTO создания в сущность BlogPost
     * @param dto DTO для создания блог-поста
     * @param imageFile загружаемое изображение (может быть null)
     * @return новая сущность BlogPost
     * @throws IOException если произошла ошибка при загрузке изображения
     */
    public BlogPost toEntity(BlogPostCreateDto dto, MultipartFile imageFile) throws IOException {
        BlogPost entity = new BlogPost();
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setExcerpt(dto.getExcerpt());
        entity.setPostType(dto.getPostType());

        // Обработка изображения, если оно предоставлено
        if (imageFile != null && !imageFile.isEmpty()) {
            ImgurService.UploadResult uploadResult = imgurService.uploadImage(imageFile);
            entity.setImagePath(uploadResult.getUrl());
            entity.setImageDeleteHash(uploadResult.getDeleteHash());
        } else if (dto.getImagePath() != null && !dto.getImagePath().isEmpty()) {
            // Если изображение предоставлено через URL (например, при редактировании без загрузки нового файла)
            entity.setImagePath(dto.getImagePath());
        }

        // Установка временных меток
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return entity;
    }

    /**
     * Обновляет существующую сущность BlogPost данными из DTO
     * @param entity существующая сущность для обновления
     * @param dto DTO с новыми данными
     * @param imageFile новое изображение (может быть null)
     * @throws IOException если произошла ошибка при загрузке изображения
     */
    public void updateEntity(BlogPost entity, BlogPostCreateDto dto, MultipartFile imageFile) throws IOException {
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setExcerpt(dto.getExcerpt());
        entity.setPostType(dto.getPostType());

        // Обработка изображения, если новое изображение предоставлено
        if (imageFile != null && !imageFile.isEmpty()) {
            // Если у сущности уже есть изображение, удаляем его
            if (entity.getImageDeleteHash() != null && !entity.getImageDeleteHash().isEmpty()) {
                imgurService.deleteImage(entity.getImageDeleteHash());
            }

            // Загружаем новое изображение
            ImgurService.UploadResult uploadResult = imgurService.uploadImage(imageFile);
            entity.setImagePath(uploadResult.getUrl());
            entity.setImageDeleteHash(uploadResult.getDeleteHash());
        } else if (dto.getImagePath() != null && !dto.getImagePath().isEmpty() &&
                !dto.getImagePath().equals(entity.getImagePath())) {
            // Если URL изображения изменился, но новый файл не был загружен
            entity.setImagePath(dto.getImagePath());
        }

        // Обновление временной метки
        entity.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Конвертирует сущность BlogPost в DTO для списка
     * @param entity сущность BlogPost
     * @return DTO для отображения в списке
     */
    public BlogPostListDto toListDto(BlogPost entity) {
        return new BlogPostListDto(
                entity.getId(),
                entity.getTitle(),
                entity.getExcerpt(),
                entity.getImagePath(),
                entity.getPostType(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Конвертирует сущность BlogPost в DTO для детального отображения
     * @param entity сущность BlogPost
     * @return DTO для детального отображения
     */
    public BlogPostDetailDto toDetailDto(BlogPost entity) {
        return new BlogPostDetailDto(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getExcerpt(),
                entity.getImagePath(),
                entity.getPostType(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Конвертирует сущность BlogPost в DTO для создания/обновления
     * Может использоваться для формы редактирования
     * @param entity сущность BlogPost
     * @return DTO для создания/обновления
     */
    public BlogPostCreateDto toCreateDto(BlogPost entity) {
        BlogPostCreateDto dto = new BlogPostCreateDto();
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setExcerpt(entity.getExcerpt());
        dto.setImagePath(entity.getImagePath());
        dto.setPostType(entity.getPostType());
        return dto;
    }
}