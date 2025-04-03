package com.example.cliniktour.repository;

import com.example.cliniktour.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByClinicId(Long clinicId);

    List<Appointment> findByDateBetween(LocalDate start, LocalDate end);

    List<Appointment> findByDoctorIdAndDateBetween(Long doctorId, LocalDate start, LocalDate end);

    List<Appointment> findByClinicIdAndDateBetween(Long clinicId, LocalDate start, LocalDate end);

    List<Appointment> findByEmail(String email);

    List<Appointment> findByPhone(String phone);

    List<Appointment> findByDoctorIsNotNull();

    List<Appointment> findByClinicIsNotNull();

    List<Appointment> findByDoctorIsNotNullAndDateBetween(LocalDate start, LocalDate end);

    List<Appointment> findByClinicIsNotNullAndDateBetween(LocalDate start, LocalDate end);

    int countByDoctorIsNotNull();

    int countByDoctorId(Long doctorId);

    int countByClinicIsNotNull();

    int countByClinicId(Long clinicId);

    int countByDate(LocalDate date);

    int countByServiceId(Long serviceId);

    List<Appointment> findByServiceIsNotNull();

    List<Appointment> findByServiceId(Long serviceId);

    List<Appointment> findByServiceIsNotNullAndDateBetween(LocalDate dateFrom, LocalDate dateTo);

    int countByServiceIsNotNull();
}