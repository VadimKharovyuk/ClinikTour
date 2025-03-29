package com.example.cliniktour.service;

import com.example.cliniktour.dto.ContactRequestDto;
import com.example.cliniktour.enums.ContactRequestStatus;
import com.example.cliniktour.mapper.ContactRequestMapper;
import com.example.cliniktour.model.ContactRequest;
import com.example.cliniktour.repository.ContactRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContactRequestService {

    private final ContactRequestRepository contactRequestRepository;
    private final ContactRequestMapper contactRequestMapper;

    /**
     * Получение всех контактных запросов
     */
    public List<ContactRequest> getAllContactRequests() {
        return contactRequestRepository.findAll();
    }

    /**
     * Получение всех контактных запросов с преобразованием в DTO
     */
    public List<ContactRequestDto> getAllContactRequestDtos() {
        List<ContactRequest> contactRequests = contactRequestRepository.findAll();
        return contactRequestMapper.toDtoList(contactRequests);
    }

    /**
     * Получение страницы контактных запросов с преобразованием в DTO
     */
    public Page<ContactRequestDto> getContactRequestPage(Pageable pageable) {
        Page<ContactRequest> contactRequestPage = contactRequestRepository.findAll(pageable);
        List<ContactRequestDto> contactRequestDtos = contactRequestMapper.toDtoList(contactRequestPage.getContent());
        return new PageImpl<>(contactRequestDtos, pageable, contactRequestPage.getTotalElements());
    }

    /**
     * Получение контактного запроса по ID
     */
    public Optional<ContactRequest> getContactRequestById(Long id) {
        return contactRequestRepository.findById(id);
    }

    /**
     * Получение контактного запроса по ID с преобразованием в DTO
     */
    public Optional<ContactRequestDto> getContactRequestDtoById(Long id) {
        return contactRequestRepository.findById(id)
                .map(contactRequestMapper::toDto);
    }

    /**
     * Получение контактных запросов по статусу
     */
    public List<ContactRequestDto> getContactRequestsByStatus(ContactRequestStatus status) {
        List<ContactRequest> contactRequests = contactRequestRepository.findByStatus(status);
        return contactRequestMapper.toDtoList(contactRequests);
    }

    /**
     * Получение страницы контактных запросов по статусу
     */
    public Page<ContactRequestDto> getContactRequestsByStatus(ContactRequestStatus status, Pageable pageable) {
        Page<ContactRequest> contactRequestPage = contactRequestRepository.findByStatus(status, pageable);
        List<ContactRequestDto> contactRequestDtos = contactRequestMapper.toDtoList(contactRequestPage.getContent());
        return new PageImpl<>(contactRequestDtos, pageable, contactRequestPage.getTotalElements());
    }

    /**
     * Сохранение нового контактного запроса
     */
    @Transactional
    public ContactRequest saveContactRequest(ContactRequest contactRequest) {
        return contactRequestRepository.save(contactRequest);
    }

    /**
     * Сохранение контактного запроса из DTO
     */
    @Transactional
    public ContactRequest saveContactRequestFromDto(ContactRequestDto dto) {
        ContactRequest contactRequest = contactRequestMapper.toEntity(dto);
        return contactRequestRepository.save(contactRequest);
    }

    /**
     * Создание нового контактного запроса из формы
     */
    @Transactional
    public ContactRequest createContactRequest(String firstName, String lastName, String email, String phone, String message) {
        ContactRequest contactRequest = ContactRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .message(message)
                .createdAt(LocalDateTime.now())
                .status(ContactRequestStatus.NEW)
                .build();

        return contactRequestRepository.save(contactRequest);
    }

    /**
     * Обновление статуса контактного запроса
     */
    @Transactional
    public ContactRequest updateContactRequestStatus(Long id, ContactRequestStatus status) {
        Optional<ContactRequest> contactRequestOpt = contactRequestRepository.findById(id);
        if (contactRequestOpt.isPresent()) {
            ContactRequest contactRequest = contactRequestOpt.get();
            contactRequest.setStatus(status);
            return contactRequestRepository.save(contactRequest);
        }
        return null;
    }

    /**
     * Обновление контактного запроса из DTO
     */
    @Transactional
    public ContactRequest updateContactRequestFromDto(ContactRequestDto dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("ID контактного запроса не может быть null при обновлении");
        }

        Optional<ContactRequest> existingContactRequestOpt = contactRequestRepository.findById(dto.getId());
        if (existingContactRequestOpt.isEmpty()) {
            throw new RuntimeException("Контактный запрос с ID " + dto.getId() + " не найден");
        }

        ContactRequest contactRequest = contactRequestMapper.updateEntityFromDto(dto, existingContactRequestOpt.get());
        return contactRequestRepository.save(contactRequest);
    }

    /**
     * Удаление контактного запроса
     */
    @Transactional
    public void deleteContactRequest(Long id) {
        contactRequestRepository.deleteById(id);
    }

    /**
     * Получение количества новых запросов
     */
    public long countNewContactRequests() {
        return contactRequestRepository.countByStatus(ContactRequestStatus.NEW);
    }

    /**
     * Поиск контактных запросов по имени или email
     */
    public List<ContactRequestDto> searchContactRequests(String searchTerm) {
        List<ContactRequest> contactRequests = contactRequestRepository.findByFirstNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                searchTerm, searchTerm);
        return contactRequestMapper.toDtoList(contactRequests);
    }

    public List<ContactRequestDto> getLatestContactRequests(int limit) {
        Page<ContactRequest> contactRequestPage = contactRequestRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt")));
        return contactRequestMapper.toDtoList(contactRequestPage.getContent());
    }
}