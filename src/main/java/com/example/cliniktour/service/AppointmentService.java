package com.example.cliniktour.service;

import com.example.cliniktour.dto.ClinicConsultationDTO;
import com.example.cliniktour.dto.ConsultationRequestDto;
import com.example.cliniktour.dto.DoctorAppointmentDTO;
import com.example.cliniktour.dto.ServiceCreateAppointmentDTO;
import com.example.cliniktour.mapper.AppointmentMapper;
import com.example.cliniktour.model.Appointment;
import com.example.cliniktour.model.Clinic;
import com.example.cliniktour.model.Department;
import com.example.cliniktour.model.Doctor;
import com.example.cliniktour.repository.*;
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
    private final DepartmentRepository departmentRepository;
    private final ServiceRepository serviceRepository;

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
     *
     * @param doctorId ID доктора
     * @return количество записей к доктору
     */
    public int countDoctorAppointments(Long doctorId) {
        return appointmentRepository.countByDoctorId(doctorId);
    }

    /**
     * Подсчитывает количество консультаций клиник
     *
     * @return количество консультаций клиник
     */
    public int countClinicConsultations() {
        return appointmentRepository.countByClinicIsNotNull();
    }

    /**
     * Подсчитывает количество консультаций конкретной клиники
     *
     * @param clinicId ID клиники
     * @return количество консультаций клиники
     */
    public int countClinicConsultations(Long clinicId) {
        return appointmentRepository.countByClinicId(clinicId);
    }

    /**
     * Подсчитывает количество записей на сегодня
     *
     * @return количество записей на сегодня
     */
    public int countTodayAppointments() {
        LocalDate today = LocalDate.now();
        return appointmentRepository.countByDate(today);
    }

    @Transactional
    public Appointment createConsultationRequest(ConsultationRequestDto dto) {
        Clinic clinic = null;
        Department department = null;

        // Получаем клинику, если ID предоставлен
        if (dto.getPreferredClinic() != null) {
            clinic = clinicRepository.findById(dto.getPreferredClinic())
                    .orElseThrow(() -> new RuntimeException("Клиника не найдена"));
        }

        // Получаем отделение, если ID предоставлен
        if (dto.getDepartmentId() != null) {
            department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Отделение не найдено"));
        }

        // Используем новый метод маппера для создания заявки
        Appointment appointment = appointmentMapper.toConsultationAppointment(dto, clinic, department);
        return appointmentRepository.save(appointment);
    }


    @Transactional
    public Optional<Appointment> createServiceAppointment(ServiceCreateAppointmentDTO dto) {
        Optional<com.example.cliniktour.model.Service> serviceOpt = serviceRepository.findById(dto.getServiceId());
        if (serviceOpt.isPresent()) {
            // Создаем запись на приём
            Appointment appointment = appointmentMapper.toEntity(dto, serviceOpt.get());
            appointment = appointmentRepository.save(appointment);

            // Возвращаем созданную запись
            return Optional.of(appointment);
        }

        return Optional.empty();
    }

    // Получение всех заявок на услуги
    public List<Appointment> getAllServiceAppointments() {
        return appointmentRepository.findByServiceIsNotNull();
    }

    // Получение заявок на конкретную услугу
    public List<Appointment> getServiceAppointments(Long serviceId) {
        return appointmentRepository.findByServiceId(serviceId);
    }

    // Получение заявок на услуги в указанном диапазоне дат
    public List<Appointment> getServiceAppointmentsByDateRange(LocalDate dateFrom, LocalDate dateTo) {
        return appointmentRepository.findByServiceIsNotNullAndDateBetween(dateFrom, dateTo);
    }

    // Подсчет количества заявок на услуги
    public int countServiceAppointments() {
        return appointmentRepository.countByServiceIsNotNull();
    }

    // Подсчет количества заявок на конкретную услугу
    public int countServiceAppointments(Long serviceId) {
        return appointmentRepository.countByServiceId(serviceId);
    }
}