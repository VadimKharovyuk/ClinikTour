package com.example.cliniktour.service;

import com.example.cliniktour.dto.ClinicDto;
import com.example.cliniktour.mapper.ClinicMapper;
import com.example.cliniktour.model.Clinic;

import com.example.cliniktour.repository.ClinicRepository;
import com.example.cliniktour.util.ImgurService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final ClinicMapper clinicMapper;
    private final ImgurService imgurService;


    public List<ClinicDto> getAllClinicsList() {
        List<Clinic> clinics = clinicRepository.findAll();
        return clinicMapper.toDtoList(clinics);
    }
    /**
     * Получение страницы клиник с преобразованием в DTO
     */
    public Page<ClinicDto> getAllClinics(Pageable pageable) {
        Page<Clinic> clinicPage = clinicRepository.findAll(pageable);
        List<ClinicDto> clinicDtos = clinicMapper.toDtoList(clinicPage.getContent());
        return new PageImpl<>(clinicDtos, pageable, clinicPage.getTotalElements());
    }

    /**
     * Получение клиники по ID с преобразованием в DTO
     */
    public Optional<ClinicDto> getClinicDtoById(Long id) {
        return clinicRepository.findById(id)
                .map(clinicMapper::toDto);
    }

    /**
     * Получение клиники по ID без преобразования
     */
    public Optional<Clinic> getClinicById(Long id) {
        return clinicRepository.findById(id);
    }

    /**
     * Поиск клиник по названию, городу или стране
     */
    public List<ClinicDto> searchClinics(String query) {
        List<Clinic> clinics = clinicRepository.findByNameContainingIgnoreCaseOrCityContainingIgnoreCaseOrCountryContainingIgnoreCase(
                query, query, query);
        return clinicMapper.toDtoList(clinics);
    }

    /**
     * Получение страницы клиник с преобразованием в DTO
     */
    public Page<ClinicDto> getClinicPage(Pageable pageable) {
        Page<Clinic> clinicPage = clinicRepository.findAll(pageable);
        List<ClinicDto> clinicDtos = clinicMapper.toDtoList(clinicPage.getContent());
        return new PageImpl<>(clinicDtos, pageable, clinicPage.getTotalElements());
    }

    /**
     * Сохранение новой клиники или обновление существующей
     */
    @Transactional
    public Clinic saveClinic(Clinic clinic) {
        return clinicRepository.save(clinic);
    }

    /**
     * Сохранение клиники с изображением
     */
    @Transactional
    public Clinic saveClinicWithImage(Clinic clinic, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            // Если клиника обновляется и у неё уже есть изображение, удаляем старое
            if (clinic.getId() != null && clinic.getImageDeleteHash() != null) {
                imgurService.deleteImage(clinic.getImageDeleteHash());
            }

            // Загружаем новое изображение
            ImgurService.UploadResult result = imgurService.uploadImage(image);
            clinic.setImagePath(result.getUrl());
            clinic.setImageDeleteHash(result.getDeleteHash());
        }

        return clinicRepository.save(clinic);
    }

    /**
     * Удаление клиники по ID
     */
    @Transactional
    public void deleteClinic(Long id) {
        clinicRepository.findById(id).ifPresent(clinic -> {
            // Если у клиники есть изображение, удаляем его из Imgur
            if (clinic.getImageDeleteHash() != null) {
                imgurService.deleteImage(clinic.getImageDeleteHash());
            }
            clinicRepository.deleteById(id);
        });
    }

    /**
     * Получение количества клиник
     */
    public long getClinicCount() {
        return clinicRepository.count();
    }

    @Transactional
    public Clinic saveClinicFromDto(ClinicDto clinicDto, MultipartFile image) throws IOException {
        // Преобразуем DTO в сущность
        Clinic clinic;
        boolean isNew = clinicDto.getId() == null;

        if (isNew) {
            clinic = clinicMapper.toEntity(clinicDto);
        } else {
            Optional<Clinic> existingClinic = getClinicById(clinicDto.getId());
            if (existingClinic.isEmpty()) {
                throw new EntityNotFoundException("Клиника не найдена");
            }
            clinic = clinicMapper.updateEntityFromDto(clinicDto, existingClinic.get());
        }

        // Сохраняем клинику с изображением
        return saveClinicWithImage(clinic, image);
    }
    /**
     * Получение клиник по ID отделения
     */
    public List<ClinicDto> getClinicsByDepartmentId(Long departmentId) {
        List<Clinic> clinics = clinicRepository.findByDepartmentId(departmentId);
        return clinicMapper.toDtoList(clinics);
    }

    public List<ClinicDto> getLatestClinics(int limit) {
        List<Clinic> clinics = clinicRepository.findLatestClinics(PageRequest.of(0, limit));
        return clinicMapper.toDtoList(clinics);
    }

    /**
     * Получение списка всех уникальных городов из клиник
     */
    public List<String> getAllCities() {
        return clinicRepository.findAll().stream()
                .map(Clinic::getCity)
                .filter(city -> city != null && !city.isEmpty())
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * Получение списка всех уникальных стран из клиник
     */
    public List<String> getAllCountries() {
        return clinicRepository.findAll().stream()
                .map(Clinic::getCountry)
                .filter(country -> country != null && !country.isEmpty())
                .distinct()
                .sorted()
                .toList();
    }


    public void cleanAllClinicDescriptions() {
        List<Clinic> clinics = clinicRepository.findAll();
        for (Clinic clinic : clinics) {
            if (clinic.getDescriptionBlock1() != null) {
                clinic.setDescriptionBlock1(cleanHtml(clinic.getDescriptionBlock1()));
            }
            if (clinic.getDescriptionBlock2() != null) {
                clinic.setDescriptionBlock2(cleanHtml(clinic.getDescriptionBlock2()));
            }
            if (clinic.getDescriptionBlock3() != null) {
                clinic.setDescriptionBlock3(cleanHtml(clinic.getDescriptionBlock3()));
            }
            if (clinic.getDescriptionBlock4() != null) {
                clinic.setDescriptionBlock4(cleanHtml(clinic.getDescriptionBlock4()));
            }
        }
        clinicRepository.saveAll(clinics);
    }

    private String cleanHtml(String html) {
        if (html == null) return null;

        return html.replaceAll("<span\\s+[^>]*>", "")
                .replaceAll("</span>", "")
                .replaceAll("style=\"[^\"]*\"", "")
                .replaceAll("font-family:[^;]*;?", "")
                .replaceAll("font-variant-ligatures:[^;]*;?", "")
                .replaceAll("orphans:[^;]*;?", "")
                .replaceAll("widows:[^;]*;?", "")
                .replaceAll("text-decoration-thickness:[^;]*;?", "")
                .replaceAll("text-decoration-style:[^;]*;?", "")
                .replaceAll("text-decoration-color:[^;]*;?", "")
                .replaceAll("<p[^>]*>", "") // Полностью удалить открывающие теги p
                .replaceAll("</p>", " ") // Заменить закрывающие теги p на пробел
                .replaceAll("<br>", " ") // Заменить br на пробел
                .replaceAll("\\s{2,}", " ") // Убрать лишние пробелы
                .trim();
    }
}