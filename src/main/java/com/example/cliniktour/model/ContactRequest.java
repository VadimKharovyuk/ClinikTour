package com.example.cliniktour.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.example.cliniktour.enums.ContactRequestStatus;
@Entity
@Table(name = "contact_requests")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String message;

    // Дата и время создания заявки
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ContactRequestStatus  status;

    // Инициализация даты создания
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = ContactRequestStatus.NEW;
        }
    }
}

