package com.example.cliniktour.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "clinics")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Clinic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imagePath;
    private String imageDeleteHash;
    private String name;

    @Column(columnDefinition = "TEXT")
    private String descriptionBlock1;

    @Column(columnDefinition = "TEXT")
    private String descriptionBlock2;

    @Column(columnDefinition = "TEXT")
    private String descriptionBlock3;

    @Column(columnDefinition = "TEXT")
    private String descriptionBlock4;

    private Integer founded; //основания
    private String address;
    private String city;
    private String country;

    // Связь с отделениями через таблицу branches
    @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Branches> branches;

    // Связь с докторами, работающими в этой клинике
    @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Doctor> doctors;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}