package com.example.cliniktour.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "departments")//Отделение
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;


    private String imagePath;
    private String imageDeleteHash;

    // Связь с branches
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Branches> branches;

    // Связь с докторами этой специализации
    @OneToMany(mappedBy = "department")
    private List<Doctor> doctors;
}