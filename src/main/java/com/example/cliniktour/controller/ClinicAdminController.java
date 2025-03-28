package com.example.cliniktour.controller;

import com.example.cliniktour.dto.ClinicDto;
import com.example.cliniktour.mapper.ClinicMapper;
import com.example.cliniktour.model.Branches;
import com.example.cliniktour.model.Clinic;
import com.example.cliniktour.model.Department;
import com.example.cliniktour.service.BranchesService;
import com.example.cliniktour.service.ClinicService;
import com.example.cliniktour.service.DepartmentService;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/clinics")
@RequiredArgsConstructor
public class ClinicAdminController {

    private final ClinicService clinicService;
    private final DepartmentService departmentService;
    private final BranchesService branchesService;

    /**
     * Отображение списка всех клиник с пагинацией
     */
    @GetMapping
    public String listClinics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<ClinicDto> clinicPage = clinicService.getClinicPage(pageable);

        model.addAttribute("clinicPage", clinicPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sort);
        model.addAttribute("totalPages", clinicPage.getTotalPages());
        model.addAttribute("totalItems", clinicPage.getTotalElements());

        // Добавляем возможные поля для сортировки
        model.addAttribute("sortFields", new String[]{"name", "city", "country"});

        return "admin/clinics/list";
    }

    /**
     * Форма создания новой клиники
     */
    @GetMapping("/create")
    public String createClinicForm(Model model) {
        model.addAttribute("clinic", new ClinicDto());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("isNew", true);

        return "admin/clinics/form";
    }

    /**
     * Форма редактирования существующей клиники
     */
    @GetMapping("/edit/{id}")
    public String editClinicForm(@PathVariable Long id, Model model) {
        Optional<ClinicDto> clinicDto = clinicService.getClinicDtoById(id);

        if (clinicDto.isPresent()) {
            model.addAttribute("clinic", clinicDto.get());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("isNew", false);

            // Получаем ID отделений, которые уже есть у клиники
            Optional<Clinic> clinicEntity = clinicService.getClinicById(id);
            if (clinicEntity.isPresent()) {
                List<Long> selectedDepartmentIds = new ArrayList<>();

                if (clinicEntity.get().getBranches() != null) {
                    clinicEntity.get().getBranches().forEach(branch -> {
                        if (branch.getDepartment() != null) {
                            selectedDepartmentIds.add(branch.getDepartment().getId());
                        }
                    });
                }

                model.addAttribute("selectedDepartmentIds", selectedDepartmentIds);
            }

            return "admin/clinics/form";
        }

        return "redirect:/admin/clinics";
    }

    /**
     * Сохранение новой или обновление существующей клиники
     */
    @PostMapping("/save")
    public String saveClinic(
            @ModelAttribute @Valid ClinicDto clinicDto,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "selectedDepartments", required = false) List<Long> selectedDepartmentIds,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            prepareFormModel(model, clinicDto, selectedDepartmentIds);
            return "admin/clinics/form";
        }

        try {
            // Сохраняем клинику из DTO с изображением
            Clinic savedClinic = clinicService.saveClinicFromDto(clinicDto, image);

            // Обрабатываем отделения
            processDepartments(savedClinic, selectedDepartmentIds, clinicDto.getId() == null);

            redirectAttributes.addFlashAttribute("success", "Клиника успешно сохранена");
            return "redirect:/admin/clinics";

        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/clinics";
        } catch (IOException e) {
            prepareFormModel(model, clinicDto, selectedDepartmentIds);
            model.addAttribute("error", "Ошибка при загрузке изображения: " + e.getMessage());
            return "admin/clinics/form";
        } catch (Exception e) {
            prepareFormModel(model, clinicDto, selectedDepartmentIds);
            model.addAttribute("error", "Ошибка при сохранении клиники: " + e.getMessage());
            return "admin/clinics/form";
        }
    }

    // Вспомогательный метод для подготовки модели формы
    private void prepareFormModel(Model model, ClinicDto clinicDto, List<Long> selectedDepartmentIds) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("isNew", clinicDto.getId() == null);
        model.addAttribute("selectedDepartmentIds", selectedDepartmentIds);
    }

    // Вспомогательный метод для обработки отделений
    private void processDepartments(Clinic clinic, List<Long> departmentIds, boolean isNew) {
        if (departmentIds != null && !departmentIds.isEmpty()) {
            // Удаляем существующие связи с отделениями для обновляемой клиники
            if (!isNew) {
                branchesService.deleteByClinicId(clinic.getId());
            }

            // Добавляем новые связи с отделениями
            List<Branches> newBranches = departmentIds.stream()
                    .map(departmentService::getDepartmentById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(department -> {
                        Branches branch = new Branches();
                        branch.setClinic(clinic);
                        branch.setDepartment(department);
                        return branch;
                    })
                    .collect(Collectors.toList());

            // Сохраняем новые связи
            if (!newBranches.isEmpty()) {
                branchesService.saveAll(newBranches);
            }
        }
    }

    /**
     * Просмотр информации о клинике
     */
    @GetMapping("/view/{id}")
    public String viewClinic(@PathVariable Long id, Model model) {
        Optional<Clinic> clinic = clinicService.getClinicById(id);

        if (clinic.isPresent()) {
            model.addAttribute("clinic", clinic.get());
            return "admin/clinics/view";
        }

        return "redirect:/admin/clinics";
    }

    /**
     * Удаление клиники
     */
    @GetMapping("/delete/{id}")
    public String deleteClinic(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clinicService.deleteClinic(id);
            redirectAttributes.addFlashAttribute("success", "Клиника успешно удалена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении клиники: " + e.getMessage());
        }

        return "redirect:/admin/clinics";
    }
}