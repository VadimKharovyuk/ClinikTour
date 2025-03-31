package com.example.cliniktour.repository;

import com.example.cliniktour.model.Testimonial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {

    // Найти последние 10 отзывов
    List<Testimonial> findTop10ByOrderByCreatedAtDesc();

    // Найти отзывы по идентификатору клиники
    List<Testimonial> findByClinicIdOrderByCreatedAtDesc(Long clinicId);

    // Найти отзывы с пагинацией (уже реализовано в JpaRepository)
    Page<Testimonial> findAll(Pageable pageable);
}