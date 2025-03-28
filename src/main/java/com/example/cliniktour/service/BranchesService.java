package com.example.cliniktour.service;

import com.example.cliniktour.dto.BranchDto;
import com.example.cliniktour.mapper.BranchMapper;
import com.example.cliniktour.model.Branches;
import com.example.cliniktour.model.Clinic;
import com.example.cliniktour.model.Department;
import com.example.cliniktour.repository.BranchesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BranchesService {

    private final BranchesRepository branchesRepository;
    private final BranchMapper branchMapper;
    private final ClinicService clinicService;
    private final DepartmentService departmentService;

    /**
     * Получить все связи
     */
    public List<Branches> findAll() {
        return branchesRepository.findAll();
    }

    /**
     * Получить все связи в виде DTO
     */
    public List<BranchDto> findAllDto() {
        return branchMapper.toDtoList(branchesRepository.findAll());
    }

    /**
     * Получить связь по ID
     */
    public Optional<Branches> findById(Long id) {
        return branchesRepository.findById(id);
    }

    /**
     * Получить связь по ID в виде DTO
     */
    public Optional<BranchDto> findDtoById(Long id) {
        return branchesRepository.findById(id)
                .map(branchMapper::toDto);
    }

    /**
     * Получить все связи по ID клиники
     */
    public List<Branches> findByClinicId(Long clinicId) {
        return branchesRepository.findByClinicId(clinicId);
    }

    /**
     * Получить все связи по ID клиники в виде DTO
     */
    public List<BranchDto> findDtoByClinicId(Long clinicId) {
        return branchMapper.toDtoList(branchesRepository.findByClinicId(clinicId));
    }

    /**
     * Получить все связи по ID отделения
     */
    public List<Branches> findByDepartmentId(Long departmentId) {
        return branchesRepository.findByDepartmentId(departmentId);
    }

    /**
     * Получить все связи по ID отделения в виде DTO
     */
    public List<BranchDto> findDtoByDepartmentId(Long departmentId) {
        return branchMapper.toDtoList(branchesRepository.findByDepartmentId(departmentId));
    }

    /**
     * Получить связь по ID клиники и ID отделения
     */
    public Optional<Branches> findByClinicIdAndDepartmentId(Long clinicId, Long departmentId) {
        return branchesRepository.findByClinicIdAndDepartmentId(clinicId, departmentId);
    }

    /**
     * Проверить существует ли связь между клиникой и отделением
     */
    public boolean existsByClinicIdAndDepartmentId(Long clinicId, Long departmentId) {
        return branchesRepository.existsByClinicIdAndDepartmentId(clinicId, departmentId);
    }

    /**
     * Сохранить связь
     */
    @Transactional
    public Branches save(Branches branch) {
        return branchesRepository.save(branch);
    }

    /**
     * Сохранить список связей
     */
    @Transactional
    public List<Branches> saveAll(List<Branches> branches) {
        return branchesRepository.saveAll(branches);
    }

    /**
     * Сохранить связь из DTO
     */
    @Transactional
    public Branches saveFromDto(BranchDto dto) {
        Optional<Clinic> clinic = clinicService.getClinicById(dto.getClinicId());
        Optional<Department> department = departmentService.getDepartmentById(dto.getDepartmentId());

        if (clinic.isEmpty() || department.isEmpty()) {
            throw new IllegalArgumentException("Клиника или отделение не найдены");
        }

        Branches branch;

        if (dto.getId() == null) {
            // Создание новой связи
            branch = branchMapper.toEntity(dto, clinic.get(), department.get());
        } else {
            // Обновление существующей связи
            Optional<Branches> existingBranch = branchesRepository.findById(dto.getId());
            if (existingBranch.isEmpty()) {
                throw new IllegalArgumentException("Связь не найдена");
            }
            branch = branchMapper.updateEntity(dto, existingBranch.get(), clinic.get(), department.get());
        }

        return branchesRepository.save(branch);
    }

    /**
     * Создать связь между клиникой и отделением
     */
    @Transactional
    public Branches createBranch(Long clinicId, Long departmentId) {
        // Проверяем, существует ли уже такая связь
        if (existsByClinicIdAndDepartmentId(clinicId, departmentId)) {
            Optional<Branches> existingBranch = findByClinicIdAndDepartmentId(clinicId, departmentId);
            return existingBranch.orElse(null);
        }

        // Получаем клинику и отделение
        Optional<Clinic> clinic = clinicService.getClinicById(clinicId);
        Optional<Department> department = departmentService.getDepartmentById(departmentId);

        if (clinic.isEmpty() || department.isEmpty()) {
            throw new IllegalArgumentException("Клиника или отделение не найдены");
        }

        // Создаем и сохраняем новую связь
        Branches branch = Branches.builder()
                .clinic(clinic.get())
                .department(department.get())
                .build();

        return branchesRepository.save(branch);
    }

    /**
     * Создать связи между клиникой и несколькими отделениями
     */
    @Transactional
    public List<Branches> createBranches(Long clinicId, List<Long> departmentIds) {
        List<Branches> result = new ArrayList<>();

        for (Long departmentId : departmentIds) {
            try {
                Branches branch = createBranch(clinicId, departmentId);
                if (branch != null) {
                    result.add(branch);
                }
            } catch (Exception e) {
                // Пропускаем ошибки отдельных связей
            }
        }

        return result;
    }

    /**
     * Удалить связь по ID
     */
    @Transactional
    public void deleteById(Long id) {
        branchesRepository.deleteById(id);
    }

    /**
     * Удалить все связи по ID клиники
     */
    @Transactional
    public void deleteByClinicId(Long clinicId) {
        branchesRepository.deleteByClinicId(clinicId);
    }

    /**
     * Удалить все связи по ID отделения
     */
    @Transactional
    public void deleteByDepartmentId(Long departmentId) {
        branchesRepository.deleteByDepartmentId(departmentId);
    }

    /**
     * Удалить связь по ID клиники и ID отделения
     */
    @Transactional
    public void deleteByClinicIdAndDepartmentId(Long clinicId, Long departmentId) {
        branchesRepository.deleteByClinicIdAndDepartmentId(clinicId, departmentId);
    }

    /**
     * Подсчитать количество связей по ID клиники
     */
    public long countByClinicId(Long clinicId) {
        return branchesRepository.countByClinicId(clinicId);
    }

    /**
     * Подсчитать количество связей по ID отделения
     */
    public long countByDepartmentId(Long departmentId) {
        return branchesRepository.countByDepartmentId(departmentId);
    }
}