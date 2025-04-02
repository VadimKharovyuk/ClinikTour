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
public class CreateDoctorDto {
    private Long id; // Null для новых докторов, заполняется при обновлении
    private String fullName;
    private String title;
    private String specialization;
    private Integer yearsOfExperience;
    private String imagePath;

    // Текстовые блоки для детальной информации
    private String memberships;
    private String clinicalInterests;
    private String education;
    private String career;

    // Идентификаторы для связей
    private Long clinicId;
    private Long departmentId;


    // Дополнительные специализации
    private List<String> additionalSpecializations;
}