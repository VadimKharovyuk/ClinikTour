package com.example.cliniktour.controller;

import com.example.cliniktour.dto.DepartmentDto;
import com.example.cliniktour.model.Department;
import com.example.cliniktour.service.ClinicService;
import com.example.cliniktour.service.DepartmentService;
import com.example.cliniktour.service.DoctorService;
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
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;


    /**
     * Отображение списка всех отделений с пагинацией и поиском
     */
    @GetMapping
    public String listDepartments(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "name") String sort,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        // Если указан поисковый запрос, используем поиск
        if (search != null && !search.trim().isEmpty()) {
            List<DepartmentDto> departments = departmentService.searchDepartments(search);
            model.addAttribute("departments", departments);
            model.addAttribute("search", search);
        }
        // В остальных случаях просто показываем страницу
        else {
            Page<DepartmentDto> departmentPage = departmentService.getDepartmentPage(pageable);
            model.addAttribute("departmentPage", departmentPage);
        }

        // Добавляем общие параметры
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sort);

        // Популярные отделения для боковой панели
        model.addAttribute("popularDepartments", departmentService.getPopularDepartments(5));

        return "departments/list";
    }

    /**
     * Отображение детальной информации об отделении
     */
    @GetMapping("/{id}")
    public String viewDepartment(@PathVariable Long id, Model model) {
        Optional<Department> departmentOpt = departmentService.getDepartmentById(id);

        if (departmentOpt.isPresent()) {
            Department department = departmentOpt.get();
            model.addAttribute("department", department);

            // Формируем DTO для получения дополнительной информации
            departmentService.getDepartmentDtoById(id).ifPresent(dto -> {
                model.addAttribute("departmentDto", dto);
            });

            // Получаем врачей этого отделения
            if (department.getDoctors() != null) {
                model.addAttribute("doctors", department.getDoctors());
            }

            // Получаем клиники, в которых есть это отделение
            // Извлекаем из связей (branches) клиники
            if (department.getBranches() != null) {
                model.addAttribute("clinics", department.getBranches().stream()
                        .filter(branch -> branch.getClinic() != null)
                        .map(branch -> branch.getClinic())
                        .distinct()
                        .toList());
            }

            return "departments/view";
        }

        return "redirect:/departments";
    }

    /**
     * Поиск отделений
     */
    @GetMapping("/search")
    public String searchDepartments(@RequestParam String query, Model model) {
        List<DepartmentDto> departments = departmentService.searchDepartments(query);
        model.addAttribute("departments", departments);
        model.addAttribute("search", query);
        return "departments/search-results";
    }

    /**
     * Отображение популярных отделений
     */
    @GetMapping("/popular")
    public String popularDepartments(Model model) {
        List<Department> popularDepartments = departmentService.getPopularDepartments(10);
        model.addAttribute("departments", popularDepartments);
        model.addAttribute("title", "Популярные отделения");
        return "departments/popular";
    }

    /**
     * Группировка отделений по первой букве (альтернатива категориям)
     */
    @GetMapping("/by-letter/{letter}")
    public String departmentsByLetter(
            @PathVariable String letter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        // Получаем все отделения
        List<Department> allDepartments = departmentService.getAllDepartments();

        // Фильтруем по первой букве названия
        List<Department> filteredDepartments = allDepartments.stream()
                .filter(dept -> dept.getName() != null &&
                        !dept.getName().isEmpty() &&
                        dept.getName().toUpperCase().startsWith(letter.toUpperCase()))
                .toList();

        // Конвертируем в DTO
        List<DepartmentDto> departmentDtos = departmentService.getAllDepartmentDtos().stream()
                .filter(dto -> dto.getName() != null &&
                        !dto.getName().isEmpty() &&
                        dto.getName().toUpperCase().startsWith(letter.toUpperCase()))
                .toList();

        model.addAttribute("departments", departmentDtos);
        model.addAttribute("letter", letter);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        // Получаем все первые буквы для алфавитной навигации
        List<String> alphabet = allDepartments.stream()
                .map(dept -> dept.getName())
                .filter(name -> name != null && !name.isEmpty())
                .map(name -> name.substring(0, 1).toUpperCase())
                .distinct()
                .sorted()
                .toList();

        model.addAttribute("alphabet", alphabet);

        return "departments/by-letter";
    }
}