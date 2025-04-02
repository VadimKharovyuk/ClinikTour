package com.example.cliniktour.service;

import com.example.cliniktour.dto.testimonial.TestimonialCreateDTO;
import com.example.cliniktour.dto.testimonial.TestimonialDetailDTO;
import com.example.cliniktour.dto.testimonial.TestimonialListDTO;
import com.example.cliniktour.mapper.TestimonialMapper;
import com.example.cliniktour.model.Clinic;
import com.example.cliniktour.model.Testimonial;
import com.example.cliniktour.repository.ClinicRepository;

import com.example.cliniktour.repository.TestimonialRepository;
import com.example.cliniktour.util.ImgurService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestimonialService {

    private final TestimonialRepository testimonialRepository;
    private final ClinicRepository clinicRepository;
    private final TestimonialMapper testimonialMapper;
    private final ImgurService imgurService;

    /**
     * Создает новый отзыв с возможностью загрузки фото
     */
    @Transactional
    public TestimonialDetailDTO createTestimonial(TestimonialCreateDTO dto, MultipartFile photoFile) {
        Clinic clinic = clinicRepository.findById(dto.getClinicId())
                .orElseThrow(() -> {
                    log.warn("Попытка создать отзыв для несуществующей клиники с ID: {}", dto.getClinicId());
                    return new EntityNotFoundException("Клиника с ID " + dto.getClinicId() + " не найдена");
                });

        Testimonial testimonial = testimonialMapper.toEntity(dto, clinic, photoFile);
        testimonial = testimonialRepository.save(testimonial);

        log.info("Отзыв успешно создан с ID: {}", testimonial.getId());
        return testimonialMapper.toDetailDto(testimonial);
    }

    /**
     * Возвращает список всех отзывов с пагинацией
     */
    @Transactional(readOnly = true)
    public Page<TestimonialListDTO> getAllTestimonials(Pageable pageable) {
        log.info("Получение списка отзывов с пагинацией: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        return testimonialRepository.findAll(pageable)
                .map(testimonialMapper::toListDto);
    }

    /**
     * Возвращает список последних отзывов
     */
    @Transactional(readOnly = true)
    public List<TestimonialListDTO> getRecentTestimonials(int limit) {
        log.info("Получение {} последних отзывов", limit);

        return testimonialRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(testimonialMapper::toListDto)
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список отзывов для конкретной клиники
     */
    @Transactional(readOnly = true)
    public List<TestimonialListDTO> getTestimonialsByClinic(Long clinicId) {
        return testimonialRepository.findByClinicIdOrderByCreatedAtDesc(clinicId)
                .stream()
                .map(testimonialMapper::toListDto)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список отзывов для конкретной клиники
     */
    @Transactional(readOnly = true)
    public List<TestimonialListDTO> getTestimonialsByClinicId(Long clinicId) {
        List<Testimonial> testimonials = testimonialRepository.findByClinicIdOrderByCreatedAtDesc(clinicId);
        return testimonials.stream()
                .map(testimonial -> {
                    try {
                        return testimonialMapper.toListDto(testimonial);
                    } catch (Exception e) {
                        log.warn("Ошибка при маппинге отзыва ID {}: {}", testimonial.getId(), e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает детальную информацию о конкретном отзыве
     */
    @Transactional(readOnly = true)
    public TestimonialDetailDTO getTestimonialById(Long id) {
        try {
            Testimonial testimonial = testimonialRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Попытка получить несуществующий отзыв с ID: {}", id);
                        return new EntityNotFoundException("Отзыв с ID " + id + " не найден");
                    });

            return testimonialMapper.toDetailDto(testimonial);
        } catch (EntityNotFoundException e) {
            throw e; // Пробрасываем дальше для обработки в контроллере
        } catch (Exception e) {
            log.error("Ошибка при получении отзыва с ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Не удалось получить данные отзыва", e);
        }
    }


    /**
     * Удаляет отзыв и его фото, если оно есть
     */
    @Transactional
    public void deleteTestimonial(Long id) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Попытка удалить несуществующий отзыв с ID: {}", id);
                    return new EntityNotFoundException("Отзыв с ID " + id + " не найден");
                });

        // Удаляем фото, если оно есть
        if (testimonial.getClientPhotoDeleteHash() != null) {
            boolean deleted = imgurService.deleteImage(testimonial.getClientPhotoDeleteHash());
            if (!deleted) {
                log.warn("Не удалось удалить фото отзыва с ID: {}", id);
            }
        }

        testimonialRepository.delete(testimonial);

    }

    /**
     * Удаляет только фото отзыва
     */
    @Transactional
    public boolean deleteTestimonialPhoto(Long id) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Попытка удалить фото у несуществующего отзыва с ID: {}", id);
                    return new EntityNotFoundException("Отзыв с ID " + id + " не найден");
                });

        if (testimonial.getClientPhotoDeleteHash() == null) {
            log.warn("У отзыва с ID: {} нет фото для удаления", id);
            return false;
        }

        boolean deleted = imgurService.deleteImage(testimonial.getClientPhotoDeleteHash());
        if (deleted) {
            testimonial.setClientPhotoUrl(null);
            testimonial.setClientPhotoDeleteHash(null);
            testimonialRepository.save(testimonial);
        } else {
            log.warn("Не удалось удалить фото отзыва с ID: {}", id);
        }

        return deleted;
    }
}