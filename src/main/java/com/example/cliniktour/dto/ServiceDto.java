package com.example.cliniktour.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDto {
    private Long id;
    private String title;
    private String description;
    private String imagePath;
    private BigDecimal price;


    private String shortDescription; // Сокращенное описание для карточек
    private String formattedPrice; // Цена, отформатированная для отображения (например, "1 000 ₽")
}