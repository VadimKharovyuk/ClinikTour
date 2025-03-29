package com.example.cliniktour.repository;

import com.example.cliniktour.model.Clinic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {

    /**
     * Поиск клиник по названию, городу или стране
     */
    List<Clinic> findByNameContainingIgnoreCaseOrCityContainingIgnoreCaseOrCountryContainingIgnoreCase(
            String name, String city, String country);

    /**
     * Поиск клиник по городу
     */
    List<Clinic> findByCityIgnoreCase(String city);

    /**
     * Поиск клиник по стране
     */
    List<Clinic> findByCountryIgnoreCase(String country);

    /**
     * Поиск клиник, имеющих определенное отделение
     */
    @Query("SELECT DISTINCT c FROM Clinic c JOIN c.branches b JOIN b.department d WHERE d.name = :departmentName")
    List<Clinic> findByDepartmentName(@Param("departmentName") String departmentName);

    /**
     * Поиск клиник, имеющих определенное отделение (с пагинацией)
     */
    @Query("SELECT DISTINCT c FROM Clinic c JOIN c.branches b JOIN b.department d WHERE d.name = :departmentName")
    Page<Clinic> findByDepartmentName(@Param("departmentName") String departmentName, Pageable pageable);

    /**
     * Поиск по нескольким параметрам
     */
    @Query("SELECT DISTINCT c FROM Clinic c " +
            "LEFT JOIN c.branches b " +
            "LEFT JOIN b.department d " +
            "WHERE (:city IS NULL OR c.city = :city) " +
            "AND (:country IS NULL OR c.country = :country) " +
            "AND (:departmentName IS NULL OR d.name = :departmentName)")
    List<Clinic> searchClinics(
            @Param("city") String city,
            @Param("country") String country,
            @Param("departmentName") String departmentName);


    @Query("SELECT c FROM Clinic c JOIN c.branches b WHERE b.department.id = :departmentId")
    List<Clinic> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT c FROM Clinic c ORDER BY c.id DESC")
    List<Clinic> findLatestClinics(Pageable pageable);

}