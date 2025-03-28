package com.example.cliniktour.controller;

import com.example.cliniktour.dto.DoctorDto;
import com.example.cliniktour.service.ClinicService;
import com.example.cliniktour.service.DepartmentService;
import com.example.cliniktour.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/admin/doctors")
@RequiredArgsConstructor
public class DoctorAdminController {

    private final DoctorService doctorService;
    private final ClinicService clinicService;
    private final DepartmentService departmentService;

    /**
     * Отображение списка всех докторов с пагинацией
     */
    @GetMapping
    public String listDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sort,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<DoctorDto> doctorPage = doctorService.getDoctorPage(pageable);

        model.addAttribute("doctorPage", doctorPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sort);
        model.addAttribute("totalPages", doctorPage.getTotalPages());
        model.addAttribute("totalItems", doctorPage.getTotalElements());

        // Добавляем возможные поля для сортировки
        model.addAttribute("sortFields", new String[]{"fullName", "specialization", "yearsOfExperience"});

        return "admin/doctors/list";
    }

    /**
     * Форма создания нового доктора
     */
    @GetMapping("/create")
    public String createDoctorForm(Model model) {
        model.addAttribute("doctor", new DoctorDto());
        model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("isNew", true);

        return "admin/doctors/form";
    }

    /**
     * Форма редактирования существующего доктора
     */
    @GetMapping("/edit/{id}")
    public String editDoctorForm(@PathVariable Long id, Model model) {
        doctorService.getDoctorDtoById(id).ifPresent(doctor -> {
            model.addAttribute("doctor", doctor);
            model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("isNew", false);
        });

        return "admin/doctors/form";
    }

    /**
     * Сохранение доктора (нового или существующего)
     */
    @PostMapping("/save")
    public String saveDoctor(
            @ModelAttribute @Valid DoctorDto doctorDto,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "additionalSpecsString", required = false) String additionalSpecsString,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("isNew", doctorDto.getId() == null);
            return "admin/doctors/form";
        }

        try {
            // Обработка дополнительных специализаций, если они переданы в текстовом виде
            if (additionalSpecsString != null && !additionalSpecsString.isEmpty()) {
                doctorDto.setAdditionalSpecializations(
                        java.util.Arrays.asList(additionalSpecsString.split("\\r?\\n"))
                                .stream()
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .collect(java.util.stream.Collectors.toList())
                );
            }

            // Сохраняем доктора с изображением
            doctorService.saveDoctorFromDtoWithImage(doctorDto, image);

            redirectAttributes.addFlashAttribute("success", "Доктор успешно сохранен");
            return "redirect:/admin/doctors";

        } catch (IOException e) {
            model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("isNew", doctorDto.getId() == null);
            model.addAttribute("error", "Ошибка при загрузке изображения: " + e.getMessage());
            return "admin/doctors/form";
        } catch (Exception e) {
            model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("isNew", doctorDto.getId() == null);
            model.addAttribute("error", "Ошибка при сохранении доктора: " + e.getMessage());
            return "admin/doctors/form";
        }
    }

    /**
     * Просмотр информации о докторе
     */
    @GetMapping("/view/{id}")
    public String viewDoctor(@PathVariable Long id, Model model) {
        doctorService.getDoctorById(id).ifPresent(doctor -> {
            model.addAttribute("doctor", doctor);
        });

        return "admin/doctors/view";
    }

    /**
     * Удаление доктора
     */
    @GetMapping("/delete/{id}")
    public String deleteDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            doctorService.deleteDoctor(id);
            redirectAttributes.addFlashAttribute("success", "Доктор успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении доктора: " + e.getMessage());
        }

        return "redirect:/admin/doctors";
    }

    /**
     * Фильтрация докторов
     */
    @GetMapping("/search")
    public String searchDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) Long clinicId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // Здесь можно добавить логику поиска/фильтрации
        // Пока просто возвращаем все записи
        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorDto> doctorPage = doctorService.getDoctorPage(pageable);

        model.addAttribute("doctorPage", doctorPage);
        model.addAttribute("name", name);
        model.addAttribute("specialization", specialization);
        model.addAttribute("clinicId", clinicId);
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
        model.addAttribute("departments", departmentService.getAllDepartments());

        return "admin/doctors/search-results";
    }
}