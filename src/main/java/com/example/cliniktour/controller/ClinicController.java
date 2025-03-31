package com.example.cliniktour.controller;

import com.example.cliniktour.dto.ClinicDto;
import com.example.cliniktour.dto.testimonial.TestimonialDetailDTO;
import com.example.cliniktour.model.Branches;
import com.example.cliniktour.model.Clinic;
import com.example.cliniktour.model.Testimonial;
import com.example.cliniktour.service.ClinicService;
import com.example.cliniktour.service.DepartmentService;
import com.example.cliniktour.service.DoctorService;
import com.example.cliniktour.service.TestimonialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/clinics")
@RequiredArgsConstructor
public class ClinicController {

    private final ClinicService clinicService;
    private final DepartmentService departmentService;
    private final DoctorService doctorService;
    private final TestimonialService testimonialService;

    /**
     * Отображение списка всех клиник с пагинацией и фильтрацией
     */
    @GetMapping
    public String listClinics(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "name") String sort,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        Page<ClinicDto> clinicPage;

        // Если указан поисковый запрос, используем поиск
        if (search != null && !search.trim().isEmpty()) {
            List<ClinicDto> clinics = clinicService.searchClinics(search);
            model.addAttribute("clinics", clinics);
            model.addAttribute("search", search);
        }
        // Если указаны фильтры, используем их
        else if ((city != null && !city.isEmpty()) ||
                (country != null && !country.isEmpty()) ||
                departmentId != null) {
            // Здесь должна быть логика фильтрации по городу, стране и отделению
            // Пока используем обычную пагинацию
            clinicPage = clinicService.getClinicPage(pageable);
            model.addAttribute("clinicPage", clinicPage);

            if (city != null) model.addAttribute("city", city);
            if (country != null) model.addAttribute("country", country);
            if (departmentId != null) model.addAttribute("departmentId", departmentId);
        }
        // В остальных случаях просто показываем страницу
        else {
            clinicPage = clinicService.getClinicPage(pageable);
            model.addAttribute("clinicPage", clinicPage);
        }

        // Добавляем общие параметры
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sort);

        // Добавляем список отделений для фильтрации
        model.addAttribute("departments", departmentService.getAllDepartments());

        // Добавляем список городов и стран (должны быть реализованы соответствующие методы)
         model.addAttribute("cities", clinicService.getAllCities());
         model.addAttribute("countries", clinicService.getAllCountries());

        return "clinics/list";
    }

    /**
     * Отображение детальной информации о клинике
     */
    @GetMapping("/{id}")
    public String viewClinic(@PathVariable Long id, Model model) {
        Optional<Clinic> clinicOpt = clinicService.getClinicById(id);


        if (clinicOpt.isPresent()) {
            Clinic clinic = clinicOpt.get();
            model.addAttribute("clinic", clinic);

            // Получаем врачей, работающих в этой клинике
            model.addAttribute("doctors", doctorService.getDoctorsByClinicId(id));

            TestimonialDetailDTO testimonial = testimonialService.getTestimonialById(id);
            model.addAttribute("testimonial", testimonial);

            // Получаем отделения клиники
            if (clinic.getBranches() != null) {
                model.addAttribute("departments", clinic.getBranches().stream()
                        .filter(branch -> branch.getDepartment() != null)
                        .map(Branches::getDepartment)
                        .toList());
            }

            return "clinics/view";
        }

        return "redirect:/clinics";
    }

    /**
     * Поиск клиник
     */
    @GetMapping("/search")
    public String searchClinics(@RequestParam String query, Model model) {
        List<ClinicDto> clinics = clinicService.searchClinics(query);
        model.addAttribute("clinics", clinics);
        model.addAttribute("search", query);
        return "clinics/search-results";
    }

    /**
     * Фильтр клиник по отделению
     */
    @GetMapping("/by-department/{departmentId}")
    public String clinicsByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        // Здесь должен быть метод для получения клиник по ID отделения
        // Пока используем обычную пагинацию
        Page<ClinicDto> clinicPage = clinicService.getClinicPage(pageable);

        model.addAttribute("clinicPage", clinicPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("departmentId", departmentId);

        // Получаем информацию об отделении для отображения
        departmentService.getDepartmentById(departmentId).ifPresent(department -> {
            model.addAttribute("department");
        });

        return "clinics/by-department";
    }
}