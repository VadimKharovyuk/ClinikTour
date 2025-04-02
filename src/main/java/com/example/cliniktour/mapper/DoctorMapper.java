package com.example.cliniktour.mapper;
import com.example.cliniktour.dto.CreateDoctorDto;
import com.example.cliniktour.dto.DoctorDetailDto;
import com.example.cliniktour.dto.DoctorListDto;
import com.example.cliniktour.model.Clinic;
import com.example.cliniktour.model.Department;
import com.example.cliniktour.model.Doctor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DoctorMapper {

    /**
     * Преобразование сущности Doctor в DoctorListDto для списков
     */
    public DoctorListDto toListDto(Doctor doctor) {
        DoctorListDto dto = new DoctorListDto();
        dto.setId(doctor.getId());
        dto.setImagePath(doctor.getImagePath());
        dto.setFullName(doctor.getFullName());
        dto.setTitle(doctor.getTitle());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setYearsOfExperience(doctor.getYearsOfExperience());

        // Информация о клинике и отделении
        if (doctor.getClinic() != null) {
            dto.setClinicName(doctor.getClinic().getName());
        }

        if (doctor.getDepartment() != null) {
            dto.setDepartmentName(doctor.getDepartment().getName());
        }

        // Создаем короткое био из опыта работы
        StringBuilder shortBio = new StringBuilder();
        if (doctor.getYearsOfExperience() != null) {
            shortBio.append("Опыт работы: ").append(doctor.getYearsOfExperience()).append(" лет. ");
        }
        if (doctor.getSpecialization() != null) {
            shortBio.append("Специализация: ").append(doctor.getSpecialization());
        }
        dto.setShortBio(shortBio.toString());

        return dto;
    }

    /**
     * Преобразование списка сущностей Doctor в список DoctorListDto
     */
    public List<DoctorListDto> toListDtoList(List<Doctor> doctors) {
        return doctors.stream()
                .map(this::toListDto)
                .collect(Collectors.toList());
    }

    /**
     * Преобразование сущности Doctor в DoctorDetailDto для детального просмотра
     */
    public DoctorDetailDto toDetailDto(Doctor doctor) {
        DoctorDetailDto dto = new DoctorDetailDto();
        dto.setId(doctor.getId());
        dto.setImagePath(doctor.getImagePath());
        dto.setFullName(doctor.getFullName());
        dto.setTitle(doctor.getTitle());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setYearsOfExperience(doctor.getYearsOfExperience());
        dto.setMemberships(doctor.getMemberships());
        dto.setClinicalInterests(doctor.getClinicalInterests());
        dto.setEducation(doctor.getEducation());
        dto.setCareer(doctor.getCareer());
        dto.setAdditionalSpecializations(doctor.getAdditionalSpecializations());

        dto.setCreatedAt(doctor.getCreatedAt());


        // Информация о клинике и отделении
        if (doctor.getClinic() != null) {
            dto.setClinicId(doctor.getClinic().getId());
            dto.setClinicName(doctor.getClinic().getName());
        }

        if (doctor.getDepartment() != null) {
            dto.setDepartmentId(doctor.getDepartment().getId());
            dto.setDepartmentName(doctor.getDepartment().getName());
        }

        return dto;
    }

    /**
     * Преобразование CreateDoctorDto в сущность Doctor
     */
    public Doctor toEntity(CreateDoctorDto dto, Clinic clinic, Department department) {
        return Doctor.builder()
                .fullName(dto.getFullName())
                .title(dto.getTitle())
                .specialization(dto.getSpecialization())
                .yearsOfExperience(dto.getYearsOfExperience())
                .imagePath(dto.getImagePath())
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
     * Обновление сущности Doctor из CreateDoctorDto
     */
    public Doctor updateEntityFromDto(CreateDoctorDto dto, Doctor doctor, Clinic clinic, Department department) {
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
}