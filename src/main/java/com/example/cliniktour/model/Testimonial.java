package com.example.cliniktour.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "testimonials")
public class Testimonial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    @Column(name = "client_country", length = 100)
    private String clientCountry;

    @Column(name = "client_city", length = 100)
    private String clientCity;

    @Column(name = "treatment_type", length = 150)
    private String treatmentType; //Тип лечения

    // Связь с клиникой
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clinic_id", referencedColumnName = "id")
    private Clinic clinic;


    @Column(name = "rating", nullable = false)
    private Integer rating; // Оценка по шкале от 1 до 5

    @Column(name = "review_text", nullable = false, columnDefinition = "TEXT")
    private String reviewText;

    @Column(name = "client_email", length = 150)
    private String clientEmail;

    @Column(name = "client_photo_url", length = 255)
    private String clientPhotoUrl;

    @Column(name = "client_photo_delete_hash", length = 100)
    private String clientPhotoDeleteHash;

    @Column(name = "would_recommend", nullable = false)
    private Boolean wouldRecommend;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


}