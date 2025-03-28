package com.example.cliniktour.mapper;

import com.example.cliniktour.dto.ClinicDto;
import com.example.cliniktour.model.Clinic;
import com.example.cliniktour.model.Branches;
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
     *
     * @param clinic Сущность клиники
     * @param displayLimit Максимальное количество отделений для отображения
     * @return DTO клиники
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

        // Вы можете добавить дополнительную логику обработки изображений при необходимости

        return ClinicDto.builder()
                .id(clinic.getId())
                .name(clinic.getName())
                .imagePath(imagePath)
                .address(clinic.getAddress())
                .city(clinic.getCity())
                .country(clinic.getCountry())
                .founded(clinic.getFounded())
                .descriptionBlock1(clinic.getDescriptionBlock1())
                .descriptionBlock2(clinic.getDescriptionBlock2())
                .descriptionBlock3(clinic.getDescriptionBlock3())
                .descriptionBlock4(clinic.getDescriptionBlock4())
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

    /**
     * Преобразует DTO в сущность Clinic
     */
    public Clinic toEntity(ClinicDto dto) {
        if (dto == null) {
            return null;
        }

        return Clinic.builder()
                .id(dto.getId())
                .name(dto.getName())
                .imagePath(dto.getImagePath())
                .address(dto.getAddress())
                .city(dto.getCity())
                .country(dto.getCountry())
                .founded(dto.getFounded())
                .descriptionBlock1(dto.getDescriptionBlock1())
                .descriptionBlock2(dto.getDescriptionBlock2())
                .descriptionBlock3(dto.getDescriptionBlock3())
                .descriptionBlock4(dto.getDescriptionBlock4())
                .build();
    }

    /**
     * Обновляет существующую сущность Clinic данными из DTO
     */
    public Clinic updateEntityFromDto(ClinicDto dto, Clinic clinic) {
        if (dto == null || clinic == null) {
            return clinic;
        }

        clinic.setName(dto.getName());
        // Не обновляем imagePath и imageDeleteHash, так как это делается отдельно
        clinic.setAddress(dto.getAddress());
        clinic.setCity(dto.getCity());
        clinic.setCountry(dto.getCountry());
        clinic.setFounded(dto.getFounded());
        clinic.setDescriptionBlock1(dto.getDescriptionBlock1());
        clinic.setDescriptionBlock2(dto.getDescriptionBlock2());
        clinic.setDescriptionBlock3(dto.getDescriptionBlock3());
        clinic.setDescriptionBlock4(dto.getDescriptionBlock4());

        return clinic;
    }

    /**
     * Обрабатывает загрузку изображения для клиники
     *
     * @param clinic Клиника для обновления
     * @param imageFile Загружаемый файл изображения
     * @return Обновленная клиника с информацией об изображении
     * @throws IOException при ошибке обработки изображения
     */
    public Clinic processClinicImage(Clinic clinic, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            // Если у клиники уже есть изображение, удаляем его
            if (clinic.getImageDeleteHash() != null && !clinic.getImageDeleteHash().isEmpty()) {
                imgurService.deleteImage(clinic.getImageDeleteHash());
            }

            // Загружаем новое изображение
            ImgurService.UploadResult result = imgurService.uploadImage(imageFile);
            clinic.setImagePath(result.getUrl());
            clinic.setImageDeleteHash(result.getDeleteHash());
        }

        return clinic;
    }
}