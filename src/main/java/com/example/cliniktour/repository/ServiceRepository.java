package com.example.cliniktour.repository;

import com.example.cliniktour.model.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByTitleContainingIgnoreCase(String title);

    List<Service> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);


    List<Service> findTopByOrderByPriceAsc(Pageable pageable);

}
