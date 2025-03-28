package com.example.cliniktour.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "doctors")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imagePath;
    private String imageDeleteHash;

    private String fullName;
    private String title;  // Ученая степень: Д.м.н., проф. и т.д.

    private String specialization; // Основная специализация
    private Integer yearsOfExperience; // Стаж работы

    // Текстовые блоки для детальной информации
    @Column(columnDefinition = "TEXT")
    private String memberships; // Членство в организациях

    @Column(columnDefinition = "TEXT")
    private String clinicalInterests; // Области клинических интересов

    @Column(columnDefinition = "TEXT")
    private String education; // Образование

    @Column(columnDefinition = "TEXT")
    private String career; // Карьера

    // Связь с клиникой, в которой работает доктор
    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    // Связь с отделением, к которому относится доктор
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // Дополнительные интересы/специализации
    @ElementCollection
    @CollectionTable(name = "doctor_specializations",
            joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "specialization")
    private List<String> additionalSpecializations;
}