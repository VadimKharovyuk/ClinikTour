package com.example.cliniktour.mapper;

import com.example.cliniktour.dto.ClinicConsultationDTO;
import com.example.cliniktour.dto.ConsultationRequestDto;
import com.example.cliniktour.dto.DoctorAppointmentDTO;
import com.example.cliniktour.dto.ServiceCreateAppointmentDTO;
import com.example.cliniktour.model.*;
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

    public Appointment toConsultationAppointment(ConsultationRequestDto dto, Clinic clinic, Department department) {
        return Appointment.builder()
                .fullName(dto.getFullName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .message(dto.getMessage())
                .privacyAgreed(dto.isAgree())
                .clinic(clinic)
                .department(department)
                .createdAt(LocalDateTime.now())
                .build();
    }


    public Appointment toEntity(ServiceCreateAppointmentDTO dto, Service service) {
        return Appointment.builder()
                .fullName(dto.getFullName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .date(dto.getDate())
                .message(dto.getMessage())
                .privacyAgreed(dto.getPrivacyAgreed())
                .service(service)
                .build();
    }
}