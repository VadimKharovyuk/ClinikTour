package com.example.cliniktour.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAppointmentDTO {
    private String fullName;
    private String phone;
    private String email;
    private LocalDate preferredDate;
    private String message;
    private boolean agree;
    private Long doctorId;
}
