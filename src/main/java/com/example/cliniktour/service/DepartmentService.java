package com.example.cliniktour.service;

import com.example.cliniktour.dto.DepartmentDto;
import com.example.cliniktour.mapper.DepartmentMapper;
import com.example.cliniktour.model.Department;

import com.example.cliniktour.repository.DepartmentRepository;
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
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final ImgurService imgurService;

    /**
     * Получение всех отделений
     */
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    /**
     * Получение всех отделений с преобразованием в DTO
     */
    public List<DepartmentDto> getAllDepartmentDtos() {
        List<Department> departments = departmentRepository.findAll();
        return departmentMapper.toDtoList(departments);
    }

    /**
     * Получение страницы отделений с преобразованием в DTO
     */
    public Page<DepartmentDto> getDepartmentPage(Pageable pageable) {
        Page<Department> departmentPage = departmentRepository.findAll(pageable);
        List<DepartmentDto> departmentDtos = departmentMapper.toDtoList(departmentPage.getContent());
        return new PageImpl<>(departmentDtos, pageable, departmentPage.getTotalElements());
    }

    /**
     * Получение отделения по ID
     */
    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }

    /**
     * Получение отделения по ID с преобразованием в DTO
     */
    public Optional<DepartmentDto> getDepartmentDtoById(Long id) {
        return departmentRepository.findById(id)
                .map(departmentMapper::toDto);
    }

    /**
     * Получение популярных отделений
     *
     * @param limit максимальное количество отделений для возврата
     * @return список популярных отделений
     */
    public List<Department> getPopularDepartments(int limit) {
        // Здесь можно реализовать логику определения популярности, например,
        // по количеству клиник или врачей в отделении
        // Пока просто возвращаем первые N отделений
        return departmentRepository.findAll().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Поиск отделений по названию
     */
    public List<DepartmentDto> searchDepartments(String query) {
        List<Department> departments = departmentRepository.findByNameContainingIgnoreCase(query);
        return departmentMapper.toDtoList(departments);
    }

    /**
     * Сохранение отделения
     */
    @Transactional
    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    /**
     * Сохранение отделения из DTO
     */
    @Transactional
    public Department saveDepartmentFromDto(DepartmentDto dto) {
        Department department = departmentMapper.toEntity(dto);
        return departmentRepository.save(department);
    }

    /**
     * Сохранение отделения с изображением
     */
    @Transactional
    public Department saveDepartmentWithImage(Department department, MultipartFile image) throws IOException {
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

        return departmentRepository.save(department);
    }

    /**
     * Полное сохранение отделения из DTO с изображением
     */
    @Transactional
    public Department saveDepartmentFromDtoWithImage(DepartmentDto dto, MultipartFile image) throws IOException {
        Department department;

        if (dto.getId() == null) {
            // Создание нового отделения
            department = departmentMapper.toEntity(dto);
        } else {
            // Обновление существующего отделения
            Optional<Department> existingDepartment = getDepartmentById(dto.getId());
            if (existingDepartment.isEmpty()) {
                throw new RuntimeException("Отделение не найдено");
            }
            department = departmentMapper.updateEntityFromDto(dto, existingDepartment.get());
        }

        // Сохраняем с изображением
        return saveDepartmentWithImage(department, image);
    }

    /**
     * Удаление отделения по ID
     */
    @Transactional
    public void deleteDepartment(Long id) {
        departmentRepository.findById(id).ifPresent(department -> {
            // Если у отделения есть изображение, удаляем его из Imgur
            if (department.getImageDeleteHash() != null) {
                imgurService.deleteImage(department.getImageDeleteHash());
            }
            departmentRepository.deleteById(id);
        });
    }

    /**
     * Подсчет общего количества отделений
     */
    public long getDepartmentsCount() {
        return departmentRepository.count();
    }


}