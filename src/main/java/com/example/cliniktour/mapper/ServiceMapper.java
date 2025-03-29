package com.example.cliniktour.mapper;

import com.example.cliniktour.dto.ServiceDto;
import com.example.cliniktour.model.Service;
import com.example.cliniktour.util.ImgurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ServiceMapper {

    private final ImgurService imgurService; // Если есть необходимость работы с изображениями

    /**
     * Преобразует сущность Service в DTO
     */
    public ServiceDto toDto(Service service) {
        if (service == null) {
            return null;
        }

        // Форматируем цену для отображения (с разделителями тысяч и валютой)
        String formattedPrice = null;
        if (service.getPrice() != null) {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
            formattedPrice = currencyFormat.format(service.getPrice());
        }

        // Создаем сокращенное описание, если есть полное
        String shortDescription = null;
        if (service.getDescription() != null && !service.getDescription().isEmpty()) {
            // Ограничиваем до 100 символов и добавляем многоточие, если описание длиннее
            shortDescription = service.getDescription().length() > 100
                    ? service.getDescription().substring(0, 100) + "..."
                    : service.getDescription();
        }

        return ServiceDto.builder()
                .id(service.getId())
                .title(service.getTitle())
                .description(service.getDescription())
                .imagePath(service.getImagePath())
                .price(service.getPrice())
                .shortDescription(shortDescription)
                .formattedPrice(formattedPrice)
                .build();
    }

    /**
     * Преобразует список сущностей Service в список DTO
     */
    public List<ServiceDto> toDtoList(List<Service> services) {
        if (services == null) {
            return Collections.emptyList();
        }

        return services.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Преобразует DTO в сущность Service
     */
    public Service toEntity(ServiceDto dto) {
        if (dto == null) {
            return null;
        }

        return Service.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .imagePath(dto.getImagePath())
                .price(dto.getPrice())
                .build();
    }

    /**
     * Обновляет существующую сущность Service данными из DTO
     */
    public Service updateEntityFromDto(ServiceDto dto, Service service) {
        if (dto == null || service == null) {
            return service;
        }

        if (dto.getTitle() != null) {
            service.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            service.setDescription(dto.getDescription());
        }

        if (dto.getPrice() != null) {
            service.setPrice(dto.getPrice());
        }

        // Не обновляем imagePath и imageDeleteHash здесь, так как это обычно делается
        // через отдельную логику загрузки изображений

        return service;
    }

    /**
     * Обрабатывает загрузку изображения для услуги
     */
    public Service processImage(Service service, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            // Если услуга обновляется и у нее уже есть изображение, удаляем старое
            if (service.getId() != null && service.getImageDeleteHash() != null) {
                imgurService.deleteImage(service.getImageDeleteHash());
            }

            // Загружаем новое изображение
            ImgurService.UploadResult result = imgurService.uploadImage(image);
            service.setImagePath(result.getUrl());
            service.setImageDeleteHash(result.getDeleteHash());
        }

        return service;
    }
}