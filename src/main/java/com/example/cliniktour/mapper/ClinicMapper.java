package com.example.cliniktour.mapper;

import com.example.cliniktour.dto.ClinicDto;
import com.example.cliniktour.model.Clinic;
import com.example.cliniktour.util.ImgurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Component
public class ClinicMapper {

    private static final int DEFAULT_DISPLAYED_DEPARTMENTS = 4;

    private final ImgurService imgurService;

    /**
     * Преобразует сущность Clinic в DTO с полным набором отделений
     */
    public ClinicDto toDto(Clinic clinic) {
        return toDto(clinic, DEFAULT_DISPLAYED_DEPARTMENTS);
    }

    /**
     * Преобразует сущность Clinic в DTO с ограничением количества отображаемых отделений
     */
    public ClinicDto toDto(Clinic clinic, int displayLimit) {
        if (clinic == null) {
            return null;
        }

        // Получаем имена отделений
        List<String> departmentNames = Collections.emptyList();
        int totalDepartments = 0;

        if (clinic.getBranches() != null) {
            totalDepartments = clinic.getBranches().size();

            departmentNames = clinic.getBranches().stream()
                    .filter(branch -> branch.getDepartment() != null)
                    .map(branch -> branch.getDepartment().getName())
                    .limit(displayLimit)
                    .collect(Collectors.toList());
        }

        // Создаем краткое описание из первого блока, если он есть
        String shortDescription = clinic.getDescriptionBlock1() != null
                ? (clinic.getDescriptionBlock1().length() > 200
                ? clinic.getDescriptionBlock1().substring(0, 200) + "..."
                : clinic.getDescriptionBlock1())
                : "";

        // Обработка URL изображения, если необходимо
        String imagePath = clinic.getImagePath();

        // Здесь можно добавить дополнительную логику обработки изображений
        // Например, проверку доступности изображения, добавление домена к относительным URL и т.д.

        return ClinicDto.builder()
                .id(clinic.getId())
                .name(clinic.getName())
                .imagePath(imagePath)
                .address(clinic.getAddress())
                .city(clinic.getCity())
                .country(clinic.getCountry())
                .founded(clinic.getFounded())
                .shortDescription(shortDescription)
                .departmentNames(departmentNames)
                .displayedDepartmentsCount(departmentNames.size())
                .totalDepartmentsCount(totalDepartments)
                .build();
    }

    /**
     * Преобразует список сущностей Clinic в список DTO
     */
    public List<ClinicDto> toDtoList(List<Clinic> clinics) {
        return toDtoList(clinics, DEFAULT_DISPLAYED_DEPARTMENTS);
    }

    /**
     * Преобразует список сущностей Clinic в список DTO с ограничением количества отображаемых отделений
     */
    public List<ClinicDto> toDtoList(List<Clinic> clinics, int displayLimit) {
        if (clinics == null) {
            return Collections.emptyList();
        }

        return clinics.stream()
                .map(clinic -> toDto(clinic, displayLimit))
                .collect(Collectors.toList());
    }
}