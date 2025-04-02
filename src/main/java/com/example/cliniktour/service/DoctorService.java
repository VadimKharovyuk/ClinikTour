package com.example.cliniktour.service;
import com.example.cliniktour.dto.CreateDoctorDto;
import com.example.cliniktour.dto.DoctorDetailDto;
import com.example.cliniktour.dto.DoctorListDto;
import com.example.cliniktour.mapper.DoctorMapper;
import com.example.cliniktour.model.Clinic;
import com.example.cliniktour.model.Department;
import com.example.cliniktour.model.Doctor;
import com.example.cliniktour.repository.DoctorRepository;
import com.example.cliniktour.util.ImgurService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final ClinicService clinicService;
    private final DepartmentService departmentService;
    private final ImgurService imgurService;

    /**
     * Получение всех докторов
     */
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * Получение всех докторов с преобразованием в ListDto
     */
    public List<DoctorListDto> getAllDoctorListDtos() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctorMapper.toListDtoList(doctors);
    }

    /**
     * Получение страницы докторов с преобразованием в ListDto
     */
    public Page<DoctorListDto> getDoctorListPage(Pageable pageable) {
        Page<Doctor> doctorPage = doctorRepository.findAll(pageable);
        List<DoctorListDto> doctorDtos = doctorMapper.toListDtoList(doctorPage.getContent());
        return new PageImpl<>(doctorDtos, pageable, doctorPage.getTotalElements());
    }

    /**
     * Получение доктора по ID
     */
    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    /**
     * Получение доктора по ID с преобразованием в DetailDto
     */
    public Optional<DoctorDetailDto> getDoctorDetailById(Long id) {
        return doctorRepository.findById(id)
                .map(doctorMapper::toDetailDto);
    }

    /**
     * Получение докторов по ID клиники
     */
    public List<Doctor> getDoctorsByClinicId(Long clinicId) {
        return doctorRepository.findByClinicId(clinicId);
    }

    /**
     * Получение ListDto докторов по ID клиники
     */
    public List<DoctorListDto> getDoctorListDtosByClinicId(Long clinicId) {
        List<Doctor> doctors = doctorRepository.findByClinicId(clinicId);
        return doctorMapper.toListDtoList(doctors);
    }

    /**
     * Получение докторов по ID отделения
     */
    public List<Doctor> getDoctorsByDepartmentId(Long departmentId) {
        return doctorRepository.findByDepartmentId(departmentId);
    }

    /**
     * Получение ListDto докторов по ID отделения
     */
    public List<DoctorListDto> getDoctorListDtosByDepartmentId(Long departmentId) {
        List<Doctor> doctors = doctorRepository.findByDepartmentId(departmentId);
        return doctorMapper.toListDtoList(doctors);
    }

    /**
     * Получение докторов по ID клиники и ID отделения
     */
    public List<Doctor> getDoctorsByClinicIdAndDepartmentId(Long clinicId, Long departmentId) {
        return doctorRepository.findByClinicIdAndDepartmentId(clinicId, departmentId);
    }

    /**
     * Поиск докторов по имени
     */
    public List<DoctorListDto> searchDoctorsByName(String name) {
        List<Doctor> doctors = doctorRepository.findByFullNameContainingIgnoreCase(name);
        return doctorMapper.toListDtoList(doctors);
    }

    /**
     * Поиск докторов по специализации
     */
    public List<DoctorListDto> searchDoctorsBySpecialization(String specialization) {
        List<Doctor> doctors = doctorRepository.findBySpecializationContainingIgnoreCase(specialization);
        return doctorMapper.toListDtoList(doctors);
    }

    /**
     * Сохранение доктора
     */
    @Transactional
    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    /**
     * Сохранение доктора с изображением
     */
    @Transactional
    public Doctor saveDoctorWithImage(Doctor doctor, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            // Если доктор обновляется и у него уже есть изображение, удаляем старое
            if (doctor.getId() != null && doctor.getImageDeleteHash() != null) {
                imgurService.deleteImage(doctor.getImageDeleteHash());
            }

            // Загружаем новое изображение
            ImgurService.UploadResult result = imgurService.uploadImage(image);
            doctor.setImagePath(result.getUrl());
            doctor.setImageDeleteHash(result.getDeleteHash());
        }

        return doctorRepository.save(doctor);
    }

    /**
     * Сохранение доктора из CreateDto
     */
    @Transactional
    public Doctor saveDoctorFromDto(CreateDoctorDto dto) {
        // Получаем клинику и отделение
        Clinic clinic = null;
        Department department = null;

        if (dto.getClinicId() != null) {
            clinic = clinicService.getClinicById(dto.getClinicId())
                    .orElseThrow(() -> new RuntimeException("Клиника не найдена"));
        }

        if (dto.getDepartmentId() != null) {
            department = departmentService.getDepartmentById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Отделение не найдено"));
        }

        Doctor doctor;

        if (dto.getId() == null) {
            // Создание нового доктора
            doctor = doctorMapper.toEntity(dto, clinic, department);
        } else {
            // Обновление существующего доктора
            doctor = doctorRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Доктор не найден"));
            doctor = doctorMapper.updateEntityFromDto(dto, doctor, clinic, department);
        }

        return doctorRepository.save(doctor);
    }

    /**
     * Сохранение доктора из CreateDto с изображением
     */
    @Transactional
    public Doctor saveDoctorFromDtoWithImage(CreateDoctorDto dto, MultipartFile image) throws IOException {
        // Получаем клинику и отделение
        Clinic clinic = null;
        Department department = null;

        if (dto.getClinicId() != null) {
            clinic = clinicService.getClinicById(dto.getClinicId())
                    .orElseThrow(() -> new RuntimeException("Клиника не найдена"));
        }

        if (dto.getDepartmentId() != null) {
            department = departmentService.getDepartmentById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Отделение не найдено"));
        }

        Doctor doctor;

        if (dto.getId() == null) {
            // Создание нового доктора
            doctor = doctorMapper.toEntity(dto, clinic, department);
        } else {
            // Обновление существующего доктора
            doctor = doctorRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Доктор не найден"));
            doctor = doctorMapper.updateEntityFromDto(dto, doctor, clinic, department);
        }

        // Сохраняем с изображением
        return saveDoctorWithImage(doctor, image);
    }

    /**
     * Удаление доктора по ID
     */
    @Transactional
    public void deleteDoctor(Long id) {
        doctorRepository.findById(id).ifPresent(doctor -> {
            // Если у доктора есть изображение, удаляем его из Imgur
            if (doctor.getImageDeleteHash() != null) {
                imgurService.deleteImage(doctor.getImageDeleteHash());
            }
            doctorRepository.deleteById(id);
        });
    }

    /**
     * Подсчет общего количества докторов
     */
    public long getDoctorsCount() {
        return doctorRepository.count();
    }

    /**
     * Получение последних добавленных докторов
     */
    public List<DoctorListDto> getLatestDoctors(int limit) {
        Page<Doctor> doctorPage = doctorRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt")));
        return doctorMapper.toListDtoList(doctorPage.getContent());
    }

    /**
     * Получение CreateDoctorDto для редактирования существующего доктора
     */
    public Optional<CreateDoctorDto> getCreateDoctorDtoForEdit(Long id) {
        return doctorRepository.findById(id)
                .map(doctor -> {
                    CreateDoctorDto dto = new CreateDoctorDto();
                    dto.setId(doctor.getId());
                    dto.setFullName(doctor.getFullName());
                    dto.setTitle(doctor.getTitle());
                    dto.setSpecialization(doctor.getSpecialization());
                    dto.setYearsOfExperience(doctor.getYearsOfExperience());
                    dto.setMemberships(doctor.getMemberships());
                    dto.setClinicalInterests(doctor.getClinicalInterests());
                    dto.setEducation(doctor.getEducation());
                    dto.setCareer(doctor.getCareer());
                    dto.setAdditionalSpecializations(doctor.getAdditionalSpecializations());
                    dto.setImagePath(doctor.getImagePath());

                    if (doctor.getClinic() != null) {
                        dto.setClinicId(doctor.getClinic().getId());
                    }

                    if (doctor.getDepartment() != null) {
                        dto.setDepartmentId(doctor.getDepartment().getId());
                    }

                    return dto;
                });
    }
}