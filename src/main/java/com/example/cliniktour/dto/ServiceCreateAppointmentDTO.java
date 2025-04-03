package com.example.cliniktour.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCreateAppointmentDTO {

    @NotNull(message = "ID услуги обязателен")
    private Long serviceId;

    @NotBlank(message = "ФИО обязательно")
    private String fullName;

    @NotBlank(message = "Номер телефона обязателен")
    private String phone;

    @Email(message = "Некорректный формат email")
    private String email;

    @NotNull(message = "Дата обязательна")
    private LocalDate date;

    private String message;

    @NotNull(message = "Необходимо согласие на обработку персональных данных")
    private Boolean privacyAgreed;
}