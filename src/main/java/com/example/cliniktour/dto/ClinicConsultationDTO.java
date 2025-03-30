package com.example.cliniktour.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicConsultationDTO {
    private String fullName;
    private String phone;
    private String email;
    private String message;
    private boolean agree;
    private Long clinicId;
}