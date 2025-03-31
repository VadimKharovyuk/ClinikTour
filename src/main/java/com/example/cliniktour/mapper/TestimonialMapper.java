package com.example.cliniktour.mapper;

import com.example.cliniktour.dto.testimonial.TestimonialCreateDTO;
import com.example.cliniktour.dto.testimonial.TestimonialDetailDTO;
import com.example.cliniktour.dto.testimonial.TestimonialListDTO;
import com.example.cliniktour.model.Clinic;
import com.example.cliniktour.model.Testimonial;
import com.example.cliniktour.util.ImgurService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
@Slf4j
@Component
@RequiredArgsConstructor
public class TestimonialMapper {

    private final ImgurService imgurService;

    /**
     * Преобразует DTO в сущность Testimonial и обрабатывает загрузку фото
     */
    public Testimonial toEntity(TestimonialCreateDTO dto, Clinic clinic, MultipartFile photoFile) {
        Testimonial testimonial = Testimonial.builder()
                .clientName(dto.getClientName())
                .clientCountry(dto.getClientCountry())
                .clientCity(dto.getClientCity())
                .treatmentType(dto.getTreatmentType())
                .clinic(clinic)
                .rating(dto.getRating())
                .reviewText(dto.getReviewText())
                .clientEmail(dto.getClientEmail())
                .wouldRecommend(dto.getWouldRecommend())
                .createdAt(LocalDateTime.now())
                .build();

        // Обработка загрузки фото при наличии
        if (photoFile != null && !photoFile.isEmpty()) {
            try {
                ImgurService.UploadResult uploadResult = imgurService.uploadImage(photoFile);
                testimonial.setClientPhotoUrl(uploadResult.getUrl());
                testimonial.setClientPhotoDeleteHash(uploadResult.getDeleteHash());
            } catch (IOException e) {
                 log.error("Не удалось загрузить фото для отзыва: {}", e.getMessage());
            }
        } else if (dto.getClientPhotoUrl() != null && !dto.getClientPhotoUrl().isEmpty()) {
            testimonial.setClientPhotoUrl(dto.getClientPhotoUrl());
        }

        return testimonial;
    }

    /**
     * Преобразует сущность в DTO для списка отзывов
     */
    public TestimonialListDTO toListDto(Testimonial testimonial) {
        return TestimonialListDTO.builder()
                .id(testimonial.getId())
                .clientName(testimonial.getClientName())
                .clientCountry(testimonial.getClientCountry())
                .clientCity(testimonial.getClientCity())
                .treatmentType(testimonial.getTreatmentType())
                .clinicName(testimonial.getClinic() != null ? testimonial.getClinic().getName() : null)
                .rating(testimonial.getRating())
                .reviewText(testimonial.getReviewText())
                .wouldRecommend(testimonial.getWouldRecommend())
                .clientPhotoUrl(testimonial.getClientPhotoUrl())
                .createdAt(testimonial.getCreatedAt())
                .build();
    }

    /**
     * Преобразует сущность в DTO для детального просмотра
     */
    public TestimonialDetailDTO toDetailDto(Testimonial testimonial) {
        return TestimonialDetailDTO.builder()
                .id(testimonial.getId())
                .clientName(testimonial.getClientName())
                .clientCountry(testimonial.getClientCountry())
                .clientCity(testimonial.getClientCity())
                .treatmentType(testimonial.getTreatmentType())
                .clinicId(testimonial.getClinic() != null ? testimonial.getClinic().getId() : null)
                .clinicName(testimonial.getClinic() != null ? testimonial.getClinic().getName() : null)
                .rating(testimonial.getRating())
                .reviewText(testimonial.getReviewText())
                .clientEmail(testimonial.getClientEmail())
                .clientPhotoUrl(testimonial.getClientPhotoUrl())
                .wouldRecommend(testimonial.getWouldRecommend())
                .createdAt(testimonial.getCreatedAt())
                .build();
    }
}