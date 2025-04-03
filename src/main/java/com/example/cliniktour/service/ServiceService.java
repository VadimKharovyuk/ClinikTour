package com.example.cliniktour.service;

import com.example.cliniktour.dto.ServiceCreateAppointmentDTO;
import com.example.cliniktour.dto.ServiceDto;
import com.example.cliniktour.mapper.AppointmentMapper;
import com.example.cliniktour.mapper.ServiceMapper;
import com.example.cliniktour.model.Appointment;
import com.example.cliniktour.model.Service;
import com.example.cliniktour.repository.AppointmentRepository;
import com.example.cliniktour.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    /**
     * Получение всех услуг
     */
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    /**
     * Получение всех услуг с преобразованием в DTO
     */
    public List<ServiceDto> getAllServiceDtos() {
        List<Service> services = serviceRepository.findAll();
        return serviceMapper.toDtoList(services);
    }

    /**
     * Получение страницы услуг с преобразованием в DTO
     */
    public Page<ServiceDto> getServicePage(Pageable pageable) {
        Page<Service> servicePage = serviceRepository.findAll(pageable);
        List<ServiceDto> serviceDtos = serviceMapper.toDtoList(servicePage.getContent());
        return new PageImpl<>(serviceDtos, pageable, servicePage.getTotalElements());
    }

    /**
     * Получение услуги по ID
     */
    public Optional<Service> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    /**
     * Получение услуги по ID с преобразованием в DTO
     */
    public Optional<ServiceDto> getServiceDtoById(Long id) {
        return serviceRepository.findById(id)
                .map(serviceMapper::toDto);
    }

    /**
     * Поиск услуг по названию
     */
    public List<ServiceDto> searchServices(String query) {
        List<Service> services = serviceRepository.findByTitleContainingIgnoreCase(query);
        return serviceMapper.toDtoList(services);
    }

    /**
     * Поиск услуг по ценовому диапазону
     */
    public List<ServiceDto> searchServicesByPriceRange(Double minPrice, Double maxPrice) {
        List<Service> services = serviceRepository.findByPriceBetween(
                minPrice != null ? java.math.BigDecimal.valueOf(minPrice) : null,
                maxPrice != null ? java.math.BigDecimal.valueOf(maxPrice) : null);
        return serviceMapper.toDtoList(services);
    }

    /**
     * Сохранение услуги
     */
    @Transactional
    public Service saveService(Service service) {
        return serviceRepository.save(service);
    }

    /**
     * Сохранение услуги из DTO
     */
    @Transactional
    public Service saveServiceFromDto(ServiceDto dto) {
        Service service = serviceMapper.toEntity(dto);
        return serviceRepository.save(service);
    }

    /**
     * Сохранение услуги с изображением
     */
    @Transactional
    public Service saveServiceWithImage(Service service, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            service = serviceMapper.processImage(service, image);
        }
        return serviceRepository.save(service);
    }

    /**
     * Полное сохранение услуги из DTO с изображением
     */
    @Transactional
    public Service saveServiceFromDtoWithImage(ServiceDto dto, MultipartFile image) throws IOException {
        Service service;

        if (dto.getId() == null) {
            // Создание новой услуги
            service = serviceMapper.toEntity(dto);
        } else {
            // Обновление существующей услуги
            Optional<Service> existingService = getServiceById(dto.getId());
            if (existingService.isEmpty()) {
                throw new RuntimeException("Услуга не найдена");
            }
            service = serviceMapper.updateEntityFromDto(dto, existingService.get());
        }

        // Сохраняем с изображением
        return saveServiceWithImage(service, image);
    }

    /**
     * Удаление услуги по ID
     */
    @Transactional
    public void deleteService(Long id) {
        // Если у услуги есть изображение, удаляем его
        getServiceById(id).ifPresent(service -> {
            if (service.getImageDeleteHash() != null) {
                try {
                    // Предполагается, что imgurService доступен через serviceMapper
                    serviceMapper.processImage(service, null);
                } catch (IOException e) {
                    throw new RuntimeException("Ошибка при удалении изображения", e);
                }
            }
        });

        serviceRepository.deleteById(id);
    }

    /**
     * Количество услуг
     */
    public long getServicesCount() {
        return serviceRepository.count();
    }

    /**
     * Получение последних добавленных услуг
     */
    public List<ServiceDto> getLatestServices(int limit) {
        Page<Service> servicePage = serviceRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, limit,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id")));
        return serviceMapper.toDtoList(servicePage.getContent());
    }

    /**
     * Получение выделенных услуг (если есть такая пометка в модели)
     * Если у вас нет такого поля, вы можете просто получить первые N услуг
     */
    public List<ServiceDto> getFeaturedServices(int limit) {
        Page<Service> servicePage = serviceRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, limit));
        return serviceMapper.toDtoList(servicePage.getContent());
    }



    /**
     * Получает список популярных услуг, отсортированных по цене
     *
     * @param limit максимальное количество возвращаемых услуг
     * @return список популярных услуг
     */
    public List<Service> getPopularServices(int limit) {
        // Используем стандартный метод findAll с пагинацией и сортировкой
        return serviceRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "price"))
        ).getContent();
    }
}