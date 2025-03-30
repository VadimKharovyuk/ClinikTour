package com.example.cliniktour.service;

import com.example.cliniktour.dto.ClinicConsultationDTO;
import com.example.cliniktour.dto.DoctorAppointmentDTO;
import com.example.cliniktour.mapper.AppointmentMapper;
import com.example.cliniktour.model.Appointment;
import com.example.cliniktour.model.Clinic;
import com.example.cliniktour.model.Doctor;
import com.example.cliniktour.repository.AppointmentRepository;
import com.example.cliniktour.repository.ClinicRepository;
import com.example.cliniktour.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional
    public Appointment createDoctorAppointment(DoctorAppointmentDTO dto) {
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Доктор не найден"));

        Appointment appointment = appointmentMapper.toDoctorAppointment(dto, doctor);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment createClinicConsultation(ClinicConsultationDTO dto) {
        Clinic clinic = clinicRepository.findById(dto.getClinicId())
                .orElseThrow(() -> new RuntimeException("Клиника не найдена"));

        Appointment appointment = appointmentMapper.toClinicConsultation(dto, clinic);
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> getClinicConsultations(Long clinicId) {
        return appointmentRepository.findByClinicId(clinicId);
    }

    @Transactional
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
    public List<Appointment> getAllDoctorAppointments() {
        return appointmentRepository.findByDoctorIsNotNull();
    }

    public List<Appointment> getAllClinicConsultations() {
        return appointmentRepository.findByClinicIsNotNull();
    }

    public List<Appointment> getAppointmentsByDateRange(LocalDate start, LocalDate end) {
        return appointmentRepository.findByDateBetween(start, end);
    }

    public List<Appointment> getDoctorAppointmentsByDateRange(LocalDate start, LocalDate end) {
        return appointmentRepository.findByDoctorIsNotNullAndDateBetween(start, end);
    }

    public List<Appointment> getClinicConsultationsByDateRange(LocalDate start, LocalDate end) {
        return appointmentRepository.findByClinicIsNotNullAndDateBetween(start, end);
    }


    public int countDoctorAppointments() {
        return appointmentRepository.countByDoctorIsNotNull();
    }

    /**
     * Подсчитывает количество записей к конкретному доктору
     * @param doctorId ID доктора
     * @return количество записей к доктору
     */
    public int countDoctorAppointments(Long doctorId) {
        return appointmentRepository.countByDoctorId(doctorId);
    }

    /**
     * Подсчитывает количество консультаций клиник
     * @return количество консультаций клиник
     */
    public int countClinicConsultations() {
        return appointmentRepository.countByClinicIsNotNull();
    }

    /**
     * Подсчитывает количество консультаций конкретной клиники
     * @param clinicId ID клиники
     * @return количество консультаций клиники
     */
    public int countClinicConsultations(Long clinicId) {
        return appointmentRepository.countByClinicId(clinicId);
    }

    /**
     * Подсчитывает количество записей на сегодня
     * @return количество записей на сегодня
     */
    public int countTodayAppointments() {
        LocalDate today = LocalDate.now();
        return appointmentRepository.countByDate(today);
    }
}