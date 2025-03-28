package com.example.cliniktour.repository;

import com.example.cliniktour.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    // Найти всех докторов по ID клиники
    List<Doctor> findByClinicId(Long clinicId);

    // Найти всех докторов по ID отделения
    List<Doctor> findByDepartmentId(Long departmentId);

    // Найти всех докторов по ID клиники и ID отделения
    List<Doctor> findByClinicIdAndDepartmentId(Long clinicId, Long departmentId);

    // Поиск докторов по специализации (частичное совпадение)
    List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);

    // Поиск докторов по имени (частичное совпадение)
    List<Doctor> findByFullNameContainingIgnoreCase(String fullName);
}