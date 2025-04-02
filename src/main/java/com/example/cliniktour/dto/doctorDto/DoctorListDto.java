package com.example.cliniktour.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorListDto {
    private Long id;
    private String imagePath;
    private String fullName;
    private String title;
    private String specialization;
    private Integer yearsOfExperience;

    // Минимальная информация о связях для отображения в списке
    private String clinicName;
    private String departmentName;

    // Сокращенная информация для списков и карточек
    private String shortBio;
}