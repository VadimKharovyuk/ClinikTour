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
public class ClinicDto {
    private Long id;
    private String name;
    private String imagePath;
    private String address;
    private String city;
    private String country;
    private Integer founded;

    // Краткое описание (можно использовать для карточки клиники)
    private String shortDescription;

    // Список отделений клиники
    private List<String> departmentNames;

    // Количество отображаемых отделений (для ограничения списка)
    private int displayedDepartmentsCount;

    // Общее количество отделений (для показа "+N")
    private int totalDepartmentsCount;
}