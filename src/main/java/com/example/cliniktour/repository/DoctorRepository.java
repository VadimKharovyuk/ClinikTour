package com.example.cliniktour.repository;

import com.example.cliniktour.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Найти всех докторов по ID клиники
     */
    List<Doctor> findByClinicId(Long clinicId);

    /**
     * Найти всех докторов по ID отделения
     */
    List<Doctor> findByDepartmentId(Long departmentId);

    /**
     * Найти всех докторов по ID клиники и ID отделения
     */
    List<Doctor> findByClinicIdAndDepartmentId(Long clinicId, Long departmentId);

    /**
     * Поиск докторов по имени (неточное совпадение, без учета регистра)
     */
    List<Doctor> findByFullNameContainingIgnoreCase(String fullName);

    /**
     * Поиск докторов по специализации (неточное совпадение, без учета регистра)
     */
    List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);

    /**
     * Найти страницу докторов по ID клиники
     */
    Page<Doctor> findByClinicId(Long clinicId, Pageable pageable);

    /**
     * Найти страницу докторов по ID отделения
     */
    Page<Doctor> findByDepartmentId(Long departmentId, Pageable pageable);

    /**
     * Получить докторов по опыту работы, большему или равному указанному
     */
    List<Doctor> findByYearsOfExperienceGreaterThanEqual(Integer yearsOfExperience);

    /**
     * Получить докторов с дополнительной специализацией
     */
    @Query("SELECT d FROM Doctor d JOIN d.additionalSpecializations s WHERE s = ?1")
    List<Doctor> findByAdditionalSpecialization(String specialization);

    /**
     * Поиск докторов по комбинации критериев
     */
    @Query("SELECT d FROM Doctor d WHERE " +
            "(:name IS NULL OR LOWER(d.fullName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:specialization IS NULL OR LOWER(d.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))) AND " +
            "(:clinicId IS NULL OR d.clinic.id = :clinicId) AND " +
            "(:departmentId IS NULL OR d.department.id = :departmentId) AND " +
            "(:minExperience IS NULL OR d.yearsOfExperience >= :minExperience)")
    Page<Doctor> searchDoctors(
            String name,
            String specialization,
            Long clinicId,
            Long departmentId,
            Integer minExperience,
            Pageable pageable);
}