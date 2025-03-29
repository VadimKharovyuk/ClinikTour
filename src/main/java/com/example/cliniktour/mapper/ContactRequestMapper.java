package com.example.cliniktour.mapper;

import com.example.cliniktour.dto.ContactRequestDto;
import com.example.cliniktour.model.ContactRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContactRequestMapper {

    /**
     * Преобразует модель ContactRequest в DTO
     */
    public ContactRequestDto toDto(ContactRequest contactRequest) {
        if (contactRequest == null) {
            return null;
        }

        String fullName = (contactRequest.getFirstName() != null ? contactRequest.getFirstName() : "") +
                (contactRequest.getLastName() != null ? " " + contactRequest.getLastName() : "");

        // Используем атрибут displayName из перечисления
        String statusDisplay = contactRequest.getStatus() != null ?
                contactRequest.getStatus().getDisplayName() : null;

        return ContactRequestDto.builder()
                .id(contactRequest.getId())
                .firstName(contactRequest.getFirstName())
                .lastName(contactRequest.getLastName())
                .email(contactRequest.getEmail())
                .phone(contactRequest.getPhone())
                .message(contactRequest.getMessage())
                .createdAt(contactRequest.getCreatedAt())
                .status(contactRequest.getStatus())
                .fullName(fullName.trim())
                .statusDisplay(statusDisplay)
                .build();
    }

    /**
     * Преобразует список моделей ContactRequest в список DTO
     */
    public List<ContactRequestDto> toDtoList(List<ContactRequest> contactRequests) {
        if (contactRequests == null) {
            return null;
        }

        return contactRequests.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Преобразует DTO в модель ContactRequest
     */
    public ContactRequest toEntity(ContactRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return ContactRequest.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .message(dto.getMessage())
                .createdAt(dto.getCreatedAt())
                .status(dto.getStatus())
                .build();
    }

    /**
     * Обновляет существующую сущность ContactRequest данными из DTO
     */
    public ContactRequest updateEntityFromDto(ContactRequestDto dto, ContactRequest contactRequest) {
        if (dto == null || contactRequest == null) {
            return contactRequest;
        }

        if (dto.getFirstName() != null) {
            contactRequest.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            contactRequest.setLastName(dto.getLastName());
        }

        if (dto.getEmail() != null) {
            contactRequest.setEmail(dto.getEmail());
        }

        if (dto.getPhone() != null) {
            contactRequest.setPhone(dto.getPhone());
        }

        if (dto.getMessage() != null) {
            contactRequest.setMessage(dto.getMessage());
        }

        if (dto.getStatus() != null) {
            contactRequest.setStatus(dto.getStatus());
        }

        return contactRequest;
    }
}