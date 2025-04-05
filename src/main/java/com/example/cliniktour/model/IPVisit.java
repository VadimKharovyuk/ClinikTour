package com.example.cliniktour.model;

import jakarta.persistence.*;
import lombok.*;


import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "ip_visit")
@NoArgsConstructor
@AllArgsConstructor
public class IPVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ipAddress;
    private int visitCount;
    private String location;

    @Temporal(TemporalType.TIMESTAMP)
    private Date firstVisit;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastVisit;

    public IPVisit(String ipAddress, String location) {
        this.ipAddress = ipAddress;
        this.location = location;
        this.visitCount = 1;
        this.firstVisit = new Date();
        this.lastVisit = new Date();
    }

    public void incrementVisitCount() {
        this.visitCount++;
        this.lastVisit = new Date();
    }
}