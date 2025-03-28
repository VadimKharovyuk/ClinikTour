package com.example.cliniktour.service;

import com.example.cliniktour.dto.DoctorDto;
import com.example.cliniktour.mapper.DoctorMapper;
import com.example.cliniktour.model.Clinic;
import com.example.cliniktour.model.Department;
import com.example.cliniktour.model.Doctor;
import com.example.cliniktour.repository.DoctorRepository;
import com.example.cliniktour.util.ImgurService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final ClinicService clinicService;
    private final DepartmentService departmentService;
    private final ImgurService imgurService;

    /**
     * Получение всех докторов
     */
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * Получение всех докторов с преобразованием в DTO
     */
    public List<DoctorDto> getAllDoctorDtos() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctorMapper.toDtoList(doctors);
    }

    /**
     * Получение страницы докторов с преобразованием в DTO
     */
    public Page<DoctorDto> getDoctorPage(Pageable pageable) {
        Page<Doctor> doctorPage = doctorRepository.findAll(pageable);
        List<DoctorDto> doctorDtos = doctorMapper.toDtoList(doctorPage.getContent());
        return new PageImpl<>(doctorDtos, pageable, doctorPage.getTotalElements());
    }

    /**
     * Получение доктора по ID
     */
    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    /**
     * Получение доктора по ID с преобразованием в DTO
     */
    public Optional<DoctorDto> getDoctorDtoById(Long id) {
        return doctorRepository.findById(id)
                .map(doctorMapper::toDto);
    }

    /**
     * Получение докторов по ID клиники
     */
    public List<Doctor> getDoctorsByClinicId(Long clinicId) {
        return doctorRepository.findByClinicId(clinicId);
    }

    /**
     * Получение докторов по ID отделения
     */
    public List<Doctor> getDoctorsByDepartmentId(Long departmentId) {
        return doctorRepository.findByDepartmentId(departmentId);
    }

    /**
     * Получение докторов по ID клиники и ID отделения
     */
    public List<Doctor> getDoctorsByClinicIdAndDepartmentId(Long clinicId, Long departmentId) {
        return doctorRepository.findByClinicIdAndDepartmentId(clinicId, departmentId);
    }

    /**
     * Поиск докторов по имени
     */
    public List<DoctorDto> searchDoctorsByName(String name) {
        List<Doctor> doctors = doctorRepository.findByFullNameContainingIgnoreCase(name);
        return doctorMapper.toDtoList(doctors);
    }

    /**
     * Поиск докторов по специализации
     */
    public List<DoctorDto> searchDoctorsBySpecialization(String specialization) {
        List<Doctor> doctors = doctorRepository.findBySpecializationContainingIgnoreCase(specialization);
        return doctorMapper.toDtoList(doctors);
    }

    /**
     * Сохранение доктора
     */
    @Transactional
    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    /**
     * Сохранение доктора с изображением
     */
    @Transactional
    public Doctor saveDoctorWithImage(Doctor doctor, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            // Если доктор обновляется и у него уже есть изображение, удаляем старое
            if (doctor.getId() != null && doctor.getImageDeleteHash() != null) {
                imgurService.deleteImage(doctor.getImageDeleteHash());
            }

            // Загружаем новое изображение
            ImgurService.UploadResult result = imgurService.uploadImage(image);
            doctor.setImagePath(result.getUrl());
            doctor.setImageDeleteHash(result.getDeleteHash());
        }

        return doctorRepository.save(doctor);
    }

    /**
     * Сохранение доктора из DTO
     */
    @Transactional
    public Doctor saveDoctorFromDto(DoctorDto dto) {
        // Получаем клинику и отделение
        Clinic clinic = null;
        Department department = null;

        if (dto.getClinicId() != null) {
            clinic = clinicService.getClinicById(dto.getClinicId())
                    .orElseThrow(() -> new RuntimeException("Клиника не найдена"));
        }

        if (dto.getDepartmentId() != null) {
            department = departmentService.getDepartmentById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Отделение не найдено"));
        }

        Doctor doctor;

        if (dto.getId() == null) {
            // Создание нового доктора
            doctor = doctorMapper.toEntity(dto, clinic, department);
        } else {
            // Обновление существующего доктора
            doctor = doctorRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Доктор не найден"));
            doctor = doctorMapper.updateEntityFromDto(dto, doctor, clinic, department);
        }

        return doctorRepository.save(doctor);
    }

    /**
     * Сохранение доктора из DTO с изображением
     */
    @Transactional
    public Doctor saveDoctorFromDtoWithImage(DoctorDto dto, MultipartFile image) throws IOException {
        // Получаем клинику и отделение
        Clinic clinic = null;
        Department department = null;

        if (dto.getClinicId() != null) {
            clinic = clinicService.getClinicById(dto.getClinicId())
                    .orElseThrow(() -> new RuntimeException("Клиника не найдена"));
        }

        if (dto.getDepartmentId() != null) {
            department = departmentService.getDepartmentById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Отделение не найдено"));
        }

        Doctor doctor;

        if (dto.getId() == null) {
            // Создание нового доктора
            doctor = doctorMapper.toEntity(dto, clinic, department);
        } else {
            // Обновление существующего доктора
            doctor = doctorRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Доктор не найден"));
            doctor = doctorMapper.updateEntityFromDto(dto, doctor, clinic, department);
        }

        // Сохраняем с изображением
        return saveDoctorWithImage(doctor, image);
    }

    /**
     * Удаление доктора по ID
     */
    @Transactional
    public void deleteDoctor(Long id) {
        doctorRepository.findById(id).ifPresent(doctor -> {
            // Если у доктора есть изображение, удаляем его из Imgur
            if (doctor.getImageDeleteHash() != null) {
                imgurService.deleteImage(doctor.getImageDeleteHash());
            }
            doctorRepository.deleteById(id);
        });
    }

    /**
     * Подсчет общего количества докторов
     */
    public long getDoctorsCount() {
        return doctorRepository.count();
    }
}