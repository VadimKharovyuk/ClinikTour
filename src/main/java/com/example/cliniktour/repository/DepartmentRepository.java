package com.example.cliniktour.repository;

import com.example.cliniktour.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * Поиск отделений по названию (неточное совпадение, без учета регистра)
     */
    List<Department> findByNameContainingIgnoreCase(String name);

    /**
     * Получение популярных отделений на основе количества связанных клиник
     */
    @Query("SELECT d FROM Department d LEFT JOIN d.branches b GROUP BY d.id ORDER BY COUNT(b.id) DESC")
    List<Department> findPopularDepartmentsByClinicCount();

    /**
     * Получение популярных отделений на основе количества связанных докторов
     */
    @Query("SELECT d FROM Department d LEFT JOIN d.doctors dr GROUP BY d.id ORDER BY COUNT(dr.id) DESC")
    List<Department> findPopularDepartmentsByDoctorCount();
}