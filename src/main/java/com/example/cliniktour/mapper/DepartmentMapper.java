package com.example.cliniktour.mapper;

import com.example.cliniktour.dto.DepartmentDto;
import com.example.cliniktour.model.Branches;
import com.example.cliniktour.model.Department;
import com.example.cliniktour.model.Doctor;
import com.example.cliniktour.util.ImgurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DepartmentMapper {

    private final ImgurService imgurService;

    /**
     * Преобразует сущность Department в DTO
     */
    public DepartmentDto toDto(Department department) {
        if (department == null) {
            return null;
        }

        // Получаем названия клиник, в которых есть это отделение
        List<String> clinicNames = Collections.emptyList();
        int clinicsCount = 0;

        if (department.getBranches() != null) {
            clinicsCount = (int) department.getBranches().stream()
                    .filter(branch -> branch.getClinic() != null)
                    .map(Branches::getClinic)
                    .distinct()
                    .count();

            clinicNames = department.getBranches().stream()
                    .filter(branch -> branch.getClinic() != null)
                    .map(branch -> branch.getClinic().getName())
                    .distinct()
                    .collect(Collectors.toList());
        }

        // Получаем имена докторов этой специализации
        List<String> doctorNames = Collections.emptyList();
        int doctorsCount = 0;

        if (department.getDoctors() != null) {
            doctorsCount = department.getDoctors().size();

            doctorNames = department.getDoctors().stream()
                    .map(Doctor::getFullName)
                    .collect(Collectors.toList());
        }

        return DepartmentDto.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .imagePath(department.getImagePath())
                .clinicsCount(clinicsCount)
                .doctorsCount(doctorsCount)
                .clinicNames(clinicNames)
                .doctorNames(doctorNames)
                .build();
    }

    /**
     * Преобразует список сущностей Department в список DTO
     */
    public List<DepartmentDto> toDtoList(List<Department> departments) {
        if (departments == null) {
            return Collections.emptyList();
        }

        return departments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Преобразует DTO в сущность Department
     */
    public Department toEntity(DepartmentDto dto) {
        if (dto == null) {
            return null;
        }

        return Department.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .imagePath(dto.getImagePath())
                .build();
    }

    /**
     * Обновляет существующую сущность Department данными из DTO
     */
    public Department updateEntityFromDto(DepartmentDto dto, Department department) {
        if (dto == null || department == null) {
            return department;
        }

        department.setName(dto.getName());
        department.setDescription(dto.getDescription());
        // imagePath и imageDeleteHash не обновляем здесь, это делается отдельно

        return department;
    }

    /**
     * Обрабатывает загрузку изображения для отделения
     */
    public Department processImage(Department department, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            // Если отделение обновляется и у него уже есть изображение, удаляем старое
            if (department.getId() != null && department.getImageDeleteHash() != null) {
                imgurService.deleteImage(department.getImageDeleteHash());
            }

            // Загружаем новое изображение
            ImgurService.UploadResult result = imgurService.uploadImage(image);
            department.setImagePath(result.getUrl());
            department.setImageDeleteHash(result.getDeleteHash());
        }

        return department;
    }
}