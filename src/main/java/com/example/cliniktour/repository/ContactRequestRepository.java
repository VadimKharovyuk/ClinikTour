package com.example.cliniktour.repository;

import com.example.cliniktour.enums.ContactRequestStatus;
import com.example.cliniktour.model.ContactRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRequestRepository extends JpaRepository<ContactRequest, Long> {

    List<ContactRequest> findByStatus(ContactRequestStatus status);

    Page<ContactRequest> findByStatus(ContactRequestStatus status, Pageable pageable);

    long countByStatus(ContactRequestStatus status);

    List<ContactRequest> findByFirstNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String firstName, String email);
}