package com.example.cliniktour.controller;


import com.example.cliniktour.dto.ClinicDto;
import com.example.cliniktour.model.Department;
import com.example.cliniktour.service.ClinicService;

import com.example.cliniktour.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ClinicService clinicService;
    private final DepartmentService departmentService;

    @GetMapping("/")
    public String home(Model model) {
        // Получаем 3 популярные клиники
        Pageable topClinicsPageable = PageRequest.of(0, 3, Sort.by("id").descending());
        List<ClinicDto> topClinics = clinicService.getAllClinics(topClinicsPageable).getContent();
        model.addAttribute("topClinics", topClinics);

        // Получаем 10 популярных отделений
        List<Department> popularDepartments = departmentService.getPopularDepartments(10);
        model.addAttribute("popularDepartments", popularDepartments);

        return "home";
    }
}