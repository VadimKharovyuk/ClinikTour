package com.example.cliniktour.dto;

import com.example.cliniktour.enums.ContactRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequestDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String message;
    private LocalDateTime createdAt;
    private ContactRequestStatus status;

    // Удобные дополнительные поля для представления
    private String fullName; // Объединенные имя и фамилия
    private String statusDisplay; // Человекочитаемый статус
}