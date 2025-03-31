package com.example.cliniktour.dto.testimonial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialListDTO {
    private Long id;
    private String clientName;
    private String clientCountry;
    private String clientCity;
    private String treatmentType;
    private String clinicName;
    private Integer rating;
    private String reviewText;
    private Boolean wouldRecommend;
    private String clientPhotoUrl;
    private LocalDateTime createdAt;
}