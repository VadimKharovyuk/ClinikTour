package com.example.cliniktour.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchDto {
    private Long id;
    private Long clinicId;
    private Long departmentId;

    // Дополнительные поля для отображения информации
    private String clinicName;
    private String departmentName;
}