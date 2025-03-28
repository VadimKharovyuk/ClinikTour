package com.example.cliniktour.repository;

import com.example.cliniktour.model.Branches;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchesRepository extends JpaRepository<Branches, Long> {

    /**
     * Найти все ветви по ID клиники
     */
    List<Branches> findByClinicId(Long clinicId);

    /**
     * Найти все ветви по ID отделения
     */
    List<Branches> findByDepartmentId(Long departmentId);

    /**
     * Найти ветвь по ID клиники и ID отделения
     */
    Optional<Branches> findByClinicIdAndDepartmentId(Long clinicId, Long departmentId);

    /**
     * Проверить существует ли связь между клиникой и отделением
     */
    boolean existsByClinicIdAndDepartmentId(Long clinicId, Long departmentId);

    /**
     * Удалить все ветви по ID клиники
     */
    @Modifying
    void deleteByClinicId(Long clinicId);

    /**
     * Удалить все ветви по ID отделения
     */
    @Modifying
    void deleteByDepartmentId(Long departmentId);

    /**
     * Удалить ветвь по ID клиники и ID отделения
     */
    @Modifying
    void deleteByClinicIdAndDepartmentId(Long clinicId, Long departmentId);

    /**
     * Подсчитать количество ветвей по ID клиники
     */
    @Query("SELECT COUNT(b) FROM Branches b WHERE b.clinic.id = ?1")
    long countByClinicId(Long clinicId);

    /**
     * Подсчитать количество ветвей по ID отделения
     */
    @Query("SELECT COUNT(b) FROM Branches b WHERE b.department.id = ?1")
    long countByDepartmentId(Long departmentId);
}