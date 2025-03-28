package com.example.cliniktour.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {
    private Long id;
    private String imagePath;
    private String fullName;
    private String title;
    private String specialization;
    private Integer yearsOfExperience;

    // Текстовые блоки для детальной информации
    private String memberships;
    private String clinicalInterests;
    private String education;
    private String career;

    // Идентификаторы для связей
    private Long clinicId;
    private Long departmentId;

    // Информация для отображения
    private String clinicName;
    private String departmentName;

    // Дополнительные специализации
    private List<String> additionalSpecializations;

    // Сокращенная информация для списков и карточек
    private String shortBio;
}