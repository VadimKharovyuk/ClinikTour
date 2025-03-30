package com.example.cliniktour.controller.admin;

import com.example.cliniktour.dto.DepartmentDto;
import com.example.cliniktour.service.DepartmentService;
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
@RequestMapping("/admin/departments")
@RequiredArgsConstructor
public class DepartmentAdminController {
    private final DepartmentService departmentService;

    @GetMapping
    public String listDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<DepartmentDto> departmentPage = departmentService.getDepartmentPage(pageable);

        model.addAttribute("departmentPage", departmentPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sort);

        return "admin/departments/list";
    }

    @GetMapping("/create")
    public String createDepartmentForm(Model model) {
        model.addAttribute("department", new DepartmentDto());
        model.addAttribute("isNew", true);

        return "admin/departments/form";
    }


    @GetMapping("/edit/{id}")
    public String editDepartmentForm(@PathVariable Long id, Model model) {
        departmentService.getDepartmentDtoById(id).ifPresent(department -> {
            model.addAttribute("department", department);
            model.addAttribute("isNew", false);
        });

        return "admin/departments/form";
    }

    @GetMapping("/view/{id}")
    public String viewDepartment(@PathVariable Long id, Model model) {
        departmentService.getDepartmentById(id).ifPresent(department -> {
            model.addAttribute("department", department);
        });

        return "admin/departments/view";
    }

    @GetMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("success", "Отделение успешно удалено");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении отделения: " + e.getMessage());
        }

        return "redirect:/admin/departments";
    }

    @PostMapping("/save")
    public String saveDepartment(
            @ModelAttribute @Valid DepartmentDto departmentDto,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Проверяем ошибки валидации
        if (bindingResult.hasErrors()) {
            model.addAttribute("department", departmentDto);
            return "admin/departments/form";
        }

        try {
            // Сохраняем отделение из DTO с изображением
            departmentService.saveDepartmentFromDtoWithImage(departmentDto, image);

            // Добавляем сообщение об успехе
            redirectAttributes.addFlashAttribute("success", "Отделение успешно сохранено");

            // Перенаправляем на список отделений
            return "redirect:/admin/departments";
        } catch (IOException e) {
            // Обработка ошибки загрузки изображения
            model.addAttribute("department", departmentDto);
            model.addAttribute("error", "Ошибка при загрузке изображения: " + e.getMessage());
            return "admin/departments/form";
        } catch (RuntimeException e) {
            // Обработка других ошибок
            model.addAttribute("department", departmentDto);
            model.addAttribute("error", "Ошибка при сохранении отделения: " + e.getMessage());
            return "admin/departments/form";
        } catch (Exception e) {
            // Обработка непредвиденных ошибок
            model.addAttribute("department", departmentDto);
            model.addAttribute("error", "Произошла непредвиденная ошибка: " + e.getMessage());
            return "admin/departments/form";
        }
    }
}