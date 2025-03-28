package com.example.cliniktour.mapper;

import com.example.cliniktour.dto.DoctorDto;
import com.example.cliniktour.model.Clinic;
import com.example.cliniktour.model.Department;
import com.example.cliniktour.model.Doctor;
import com.example.cliniktour.util.ImgurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DoctorMapper {

    private final ImgurService imgurService;

    /**
     * Преобразует сущность Doctor в DTO
     */
    public DoctorDto toDto(Doctor doctor) {
        if (doctor == null) {
            return null;
        }

        DoctorDto dto = DoctorDto.builder()
                .id(doctor.getId())
                .imagePath(doctor.getImagePath())
                .fullName(doctor.getFullName())
                .title(doctor.getTitle())
                .specialization(doctor.getSpecialization())
                .yearsOfExperience(doctor.getYearsOfExperience())
                .memberships(doctor.getMemberships())
                .clinicalInterests(doctor.getClinicalInterests())
                .education(doctor.getEducation())
                .career(doctor.getCareer())
                .additionalSpecializations(
                        doctor.getAdditionalSpecializations() != null
                                ? new ArrayList<>(doctor.getAdditionalSpecializations())
                                : new ArrayList<>()
                )
                .build();

        // Добавляем информацию о клинике
        if (doctor.getClinic() != null) {
            dto.setClinicId(doctor.getClinic().getId());
            dto.setClinicName(doctor.getClinic().getName());
        }

        // Добавляем информацию об отделении
        if (doctor.getDepartment() != null) {
            dto.setDepartmentId(doctor.getDepartment().getId());
            dto.setDepartmentName(doctor.getDepartment().getName());
        }

        // Создаем краткую биографию для карточек
        dto.setShortBio(createShortBio(doctor));

        return dto;
    }

    /**
     * Преобразует список сущностей Doctor в список DTO
     */
    public List<DoctorDto> toDtoList(List<Doctor> doctors) {
        if (doctors == null) {
            return Collections.emptyList();
        }

        return doctors.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Преобразует DTO в сущность Doctor
     */
    public Doctor toEntity(DoctorDto dto, Clinic clinic, Department department) {
        if (dto == null) {
            return null;
        }

        return Doctor.builder()
                .id(dto.getId())
                .imagePath(dto.getImagePath())
                .fullName(dto.getFullName())
                .title(dto.getTitle())
                .specialization(dto.getSpecialization())
                .yearsOfExperience(dto.getYearsOfExperience())
                .memberships(dto.getMemberships())
                .clinicalInterests(dto.getClinicalInterests())
                .education(dto.getEducation())
                .career(dto.getCareer())
                .clinic(clinic)
                .department(department)
                .additionalSpecializations(dto.getAdditionalSpecializations())
                .build();
    }

    /**
     * Обновляет существующую сущность Doctor данными из DTO
     */
    public Doctor updateEntityFromDto(DoctorDto dto, Doctor doctor, Clinic clinic, Department department) {
        if (dto == null || doctor == null) {
            return doctor;
        }

        doctor.setFullName(dto.getFullName());
        doctor.setTitle(dto.getTitle());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setYearsOfExperience(dto.getYearsOfExperience());
        doctor.setMemberships(dto.getMemberships());
        doctor.setClinicalInterests(dto.getClinicalInterests());
        doctor.setEducation(dto.getEducation());
        doctor.setCareer(dto.getCareer());
        doctor.setClinic(clinic);
        doctor.setDepartment(department);
        doctor.setAdditionalSpecializations(dto.getAdditionalSpecializations());

        return doctor;
    }

    /**
     * Обрабатывает загрузку изображения для доктора
     */
    public Doctor processImage(Doctor doctor, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            // Если у доктора уже есть изображение, удаляем его
            if (doctor.getId() != null && doctor.getImageDeleteHash() != null) {
                imgurService.deleteImage(doctor.getImageDeleteHash());
            }

            // Загружаем новое изображение
            ImgurService.UploadResult result = imgurService.uploadImage(image);
            doctor.setImagePath(result.getUrl());
            doctor.setImageDeleteHash(result.getDeleteHash());
        }

        return doctor;
    }

    /**
     * Создает краткую биографию доктора для карточек
     */
    private String createShortBio(Doctor doctor) {
        StringBuilder bio = new StringBuilder();

        if (doctor.getTitle() != null && !doctor.getTitle().isEmpty()) {
            bio.append(doctor.getTitle());
            if (!doctor.getTitle().endsWith(".")) {
                bio.append(".");
            }
            bio.append(" ");
        }

        if (doctor.getYearsOfExperience() != null) {
            bio.append("Стаж работы: ").append(doctor.getYearsOfExperience()).append(" лет. ");
        }

        if (doctor.getSpecialization() != null && !doctor.getSpecialization().isEmpty()) {
            bio.append("Специализация: ").append(doctor.getSpecialization()).append(".");
        }

        return bio.toString();
    }
}