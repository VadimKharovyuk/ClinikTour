package com.example.cliniktour.mapper;

import com.example.cliniktour.dto.BranchDto;
import com.example.cliniktour.model.Branches;
import com.example.cliniktour.model.Clinic;
import com.example.cliniktour.model.Department;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BranchMapper {

    /**
     * Преобразует сущность Branches в DTO
     */
    public BranchDto toDto(Branches branch) {
        if (branch == null) {
            return null;
        }

        BranchDto dto = new BranchDto();
        dto.setId(branch.getId());

        if (branch.getClinic() != null) {
            dto.setClinicId(branch.getClinic().getId());
            dto.setClinicName(branch.getClinic().getName());
        }

        if (branch.getDepartment() != null) {
            dto.setDepartmentId(branch.getDepartment().getId());
            dto.setDepartmentName(branch.getDepartment().getName());
        }

        return dto;
    }

    /**
     * Преобразует список сущностей Branches в список DTO
     */
    public List<BranchDto> toDtoList(List<Branches> branches) {
        if (branches == null) {
            return Collections.emptyList();
        }

        return branches.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Создает новую сущность Branches из DTO и объектов Clinic и Department
     */
    public Branches toEntity(BranchDto dto, Clinic clinic, Department department) {
        if (dto == null) {
            return null;
        }

        return Branches.builder()
                .id(dto.getId())
                .clinic(clinic)
                .department(department)
                .build();
    }

    /**
     * Обновляет существующую сущность Branches данными из DTO
     */
    public Branches updateEntity(BranchDto dto, Branches branch, Clinic clinic, Department department) {
        if (dto == null || branch == null) {
            return branch;
        }

        branch.setClinic(clinic);
        branch.setDepartment(department);

        return branch;
    }
}