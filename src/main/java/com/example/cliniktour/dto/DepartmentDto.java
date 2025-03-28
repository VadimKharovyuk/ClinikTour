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
public class DepartmentDto {
    private Long id;
    private String name;
    private String description;
    private String imagePath;

    // Количество клиник с данным отделением
    private int clinicsCount;

    // Количество докторов данной специализации
    private int doctorsCount;

    // Список клиник с этим отделением (для детального просмотра)
    private List<String> clinicNames;

    // Список докторов этой специализации (для детального просмотра)
    private List<String> doctorNames;
}