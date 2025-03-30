package com.example.cliniktour.mapper;

import com.example.cliniktour.dto.ClinicConsultationDTO;
import com.example.cliniktour.dto.DoctorAppointmentDTO;
import com.example.cliniktour.model.Appointment;
import com.example.cliniktour.model.Clinic;
import com.example.cliniktour.model.Doctor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AppointmentMapper {

    public Appointment toDoctorAppointment(DoctorAppointmentDTO dto, Doctor doctor) {
        return Appointment.builder()
                .fullName(dto.getFullName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .date(dto.getPreferredDate())
                .message(dto.getMessage())
                .privacyAgreed(dto.isAgree())
                .doctor(doctor)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Appointment toClinicConsultation(ClinicConsultationDTO dto, Clinic clinic) {
        return Appointment.builder()
                .fullName(dto.getFullName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .message(dto.getMessage())
                .privacyAgreed(dto.isAgree())
                .clinic(clinic)
                .createdAt(LocalDateTime.now())
                .build();
    }
}