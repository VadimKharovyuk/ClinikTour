package com.example.cliniktour.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDetailDto {
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

    // Информация о связях для отображения
    private Long clinicId;
    private String clinicName;
    private Long departmentId;
    private String departmentName;

    // Добавляем поля для дат
    private LocalDateTime createdAt;

    // Дополнительные специализации
    private List<String> additionalSpecializations;
}