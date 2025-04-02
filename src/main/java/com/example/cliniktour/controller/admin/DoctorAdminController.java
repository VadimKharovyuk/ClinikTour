//package com.example.cliniktour.controller.admin;
//
//import com.example.cliniktour.dto.DoctorDto;
//import com.example.cliniktour.service.ClinicService;
//import com.example.cliniktour.service.DepartmentService;
//import com.example.cliniktour.service.DoctorService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.io.IOException;
//
//@Controller
//@RequestMapping("/admin/doctors")
//@RequiredArgsConstructor
//public class DoctorAdminController {
//
//    private final DoctorService doctorService;
//    private final ClinicService clinicService;
//    private final DepartmentService departmentService;
//
//    /**
//     * Отображение списка всех докторов с пагинацией
//     */
//    @GetMapping
//    public String listDoctors(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "fullName") String sort,
//            Model model) {
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
//        Page<DoctorDto> doctorPage = doctorService.getDoctorPage(pageable);
//
//        model.addAttribute("doctorPage", doctorPage);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("pageSize", size);
//        model.addAttribute("sortField", sort);
//        model.addAttribute("totalPages", doctorPage.getTotalPages());
//        model.addAttribute("totalItems", doctorPage.getTotalElements());
//
//        // Добавляем возможные поля для сортировки
//        model.addAttribute("sortFields", new String[]{"fullName", "specialization", "yearsOfExperience"});
//
//        return "admin/doctors/list";
//    }
//
//
//
//    /**
//     * Форма создания нового доктора
//     */
//    @GetMapping("/create")
//    public String createDoctorForm(Model model) {
//        model.addAttribute("doctor", new DoctorDto());
//        model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
//        model.addAttribute("departments", departmentService.getAllDepartments());
//        model.addAttribute("isNew", true);
//
//        return "admin/doctors/form";
//    }
//
//    /**
//     * Форма редактирования существующего доктора
//     */
//    @GetMapping("/edit/{id}")
//    public String editDoctorForm(@PathVariable Long id, Model model) {
//        doctorService.getDoctorDtoById(id).ifPresent(doctor -> {
//            model.addAttribute("doctor", doctor);
//            model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
//            model.addAttribute("departments", departmentService.getAllDepartments());
//            model.addAttribute("isNew", false);
//        });
//
//        return "admin/doctors/form";
//    }
//
//    /**
//     * Сохранение доктора (нового или существующего)
//     */
//    @PostMapping("/save")
//    public String saveDoctor(
//            @ModelAttribute @Valid DoctorDto doctorDto,
//            BindingResult bindingResult,
//            @RequestParam(value = "image", required = false) MultipartFile image,
//            @RequestParam(value = "additionalSpecsString", required = false) String additionalSpecsString,
//            Model model,
//            RedirectAttributes redirectAttributes) {
//
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
//            model.addAttribute("departments", departmentService.getAllDepartments());
//            model.addAttribute("isNew", doctorDto.getId() == null);
//            return "admin/doctors/form";
//        }
//
//        try {
//            // Обработка дополнительных специализаций, если они переданы в текстовом виде
//            if (additionalSpecsString != null && !additionalSpecsString.isEmpty()) {
//                doctorDto.setAdditionalSpecializations(
//                        java.util.Arrays.asList(additionalSpecsString.split("\\r?\\n"))
//                                .stream()
//                                .map(String::trim)
//                                .filter(s -> !s.isEmpty())
//                                .collect(java.util.stream.Collectors.toList())
//                );
//            }
//
//            // Сохраняем доктора с изображением
//            doctorService.saveDoctorFromDtoWithImage(doctorDto, image);
//
//            redirectAttributes.addFlashAttribute("success", "Доктор успешно сохранен");
//            return "redirect:/admin/doctors";
//
//        } catch (IOException e) {
//            model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
//            model.addAttribute("departments", departmentService.getAllDepartments());
//            model.addAttribute("isNew", doctorDto.getId() == null);
//            model.addAttribute("error", "Ошибка при загрузке изображения: " + e.getMessage());
//            return "admin/doctors/form";
//        } catch (Exception e) {
//            model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
//            model.addAttribute("departments", departmentService.getAllDepartments());
//            model.addAttribute("isNew", doctorDto.getId() == null);
//            model.addAttribute("error", "Ошибка при сохранении доктора: " + e.getMessage());
//            return "admin/doctors/form";
//        }
//    }
//
//    /**
//     * Просмотр информации о докторе
//     */
//    @GetMapping("/view/{id}")
//    public String viewDoctor(@PathVariable Long id, Model model) {
//        doctorService.getDoctorById(id).ifPresent(doctor -> {
//            model.addAttribute("doctor", doctor);
//        });
//
//        return "admin/doctors/view";
//    }
//
//    /**
//     * Удаление доктора
//     */
//    @GetMapping("/delete/{id}")
//    public String deleteDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//        try {
//            doctorService.deleteDoctor(id);
//            redirectAttributes.addFlashAttribute("success", "Доктор успешно удален");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении доктора: " + e.getMessage());
//        }
//
//        return "redirect:/admin/doctors";
//    }
//
//    /**
//     * Фильтрация докторов
//     */
//    @GetMapping("/search")
//    public String searchDoctors(
//            @RequestParam(required = false) String name,
//            @RequestParam(required = false) String specialization,
//            @RequestParam(required = false) Long clinicId,
//            @RequestParam(required = false) Long departmentId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            Model model) {
//
//        // Здесь можно добавить логику поиска/фильтрации
//        // Пока просто возвращаем все записи
//        Pageable pageable = PageRequest.of(page, size);
//        Page<DoctorDto> doctorPage = doctorService.getDoctorPage(pageable);
//
//        model.addAttribute("doctorPage", doctorPage);
//        model.addAttribute("name", name);
//        model.addAttribute("specialization", specialization);
//        model.addAttribute("clinicId", clinicId);
//        model.addAttribute("departmentId", departmentId);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("pageSize", size);
//
//        model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
//        model.addAttribute("departments", departmentService.getAllDepartments());
//
//        return "admin/doctors/search-results";
//    }
//}

package com.example.cliniktour.controller.admin;

import com.example.cliniktour.dto.CreateDoctorDto;
import com.example.cliniktour.dto.DoctorDetailDto;
import com.example.cliniktour.dto.DoctorListDto;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        Page<DoctorListDto> doctorPage = doctorService.getDoctorListPage(pageable);

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
        model.addAttribute("doctor", new CreateDoctorDto());
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
        doctorService.getCreateDoctorDtoForEdit(id).ifPresent(doctorDto -> {
            model.addAttribute("doctor", doctorDto);
            model.addAttribute("isNew", false);
        });

        model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
        model.addAttribute("departments", departmentService.getAllDepartments());

        return "admin/doctors/form";
    }

//    /**
//     * Форма редактирования существующего доктора
//     */
//    @GetMapping("/edit/{id}")
//    public String editDoctorForm(@PathVariable Long id, Model model) {
//        doctorService.getDoctorById(id).ifPresent(doctor -> {
//            // Создаем DTO для формы из сущности
//            CreateDoctorDto createDoctorDto = new CreateDoctorDto();
//            createDoctorDto.setId(doctor.getId());
//            createDoctorDto.setFullName(doctor.getFullName());
//            createDoctorDto.setTitle(doctor.getTitle());
//            createDoctorDto.setSpecialization(doctor.getSpecialization());
//            createDoctorDto.setYearsOfExperience(doctor.getYearsOfExperience());
//            createDoctorDto.setMemberships(doctor.getMemberships());
//            createDoctorDto.setClinicalInterests(doctor.getClinicalInterests());
//            createDoctorDto.setEducation(doctor.getEducation());
//            createDoctorDto.setCareer(doctor.getCareer());
//            createDoctorDto.setAdditionalSpecializations(doctor.getAdditionalSpecializations());
//            createDoctorDto.setImagePath(doctor.getImagePath());
//
//            if (doctor.getClinic() != null) {
//                createDoctorDto.setClinicId(doctor.getClinic().getId());
//            }
//
//            if (doctor.getDepartment() != null) {
//                createDoctorDto.setDepartmentId(doctor.getDepartment().getId());
//            }
//
//            model.addAttribute("doctor", createDoctorDto);
//            model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
//            model.addAttribute("departments", departmentService.getAllDepartments());
//            model.addAttribute("isNew", false);
//        });
//
//        return "admin/doctors/form";
//    }

    /**
     * Сохранение доктора (нового или существующего)
     */
    @PostMapping("/save")
    public String saveDoctor(
            @ModelAttribute @Valid CreateDoctorDto doctorDto,
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
                List<String> additionalSpecs = Arrays.stream(additionalSpecsString.split("\\r?\\n"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                doctorDto.setAdditionalSpecializations(additionalSpecs);
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
        doctorService.getDoctorDetailById(id).ifPresent(doctorDetail -> {
            model.addAttribute("doctor", doctorDetail);
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

        Pageable pageable = PageRequest.of(page, size);

        // Реализуем базовую логику поиска
        List<DoctorListDto> results = null;

        if (name != null && !name.trim().isEmpty()) {
            // Поиск по имени
            results = doctorService.searchDoctorsByName(name);
        } else if (specialization != null && !specialization.trim().isEmpty()) {
            // Поиск по специализации
            results = doctorService.searchDoctorsBySpecialization(specialization);
        } else {
            // Если нет критериев поиска, используем общую пагинацию
            Page<DoctorListDto> doctorPage = doctorService.getDoctorListPage(pageable);
            model.addAttribute("doctorPage", doctorPage);
        }

        if (results != null) {
            model.addAttribute("doctors", results);
        }

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