package com.example.cliniktour.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationRequestDto {

    private Long departmentId;

    @NotBlank(message = "ФИО обязательно для заполнения")
    private String fullName;

    @NotBlank(message = "Телефон обязателен для заполнения")
    private String phone;

    @NotBlank(message = "Email обязателен для заполнения")
    @Email(message = "Пожалуйста, введите корректный email")
    private String email;

    private Long preferredClinic;

    @NotBlank(message = "Пожалуйста, опишите вашу проблему")
    private String message;

    @AssertTrue(message = "Необходимо согласиться с обработкой персональных данных")
    private boolean agree;
}