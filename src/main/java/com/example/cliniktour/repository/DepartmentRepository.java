package com.example.cliniktour.repository;

import com.example.cliniktour.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


}