package com.example.cliniktour.controller.admin;

import com.example.cliniktour.dto.BranchDto;
import com.example.cliniktour.service.BranchesService;
import com.example.cliniktour.service.ClinicService;
import com.example.cliniktour.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/branches")
@RequiredArgsConstructor
public class BranchesAdminController {

    private final BranchesService branchesService;
    private final ClinicService clinicService;
    private final DepartmentService departmentService;

    /**
     * Отображение списка всех связей
     */
    @GetMapping
    public String listBranches(Model model) {
        List<BranchDto> branches = branchesService.findAllDto();
        model.addAttribute("branches", branches);
        return "admin/branches/list";
    }

    /**
     * Форма создания новой связи
     */
    @GetMapping("/create")
    public String createBranchForm(Model model) {
        model.addAttribute("branch", new BranchDto());
        model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("isNew", true);
        return "admin/branches/form";
    }

    /**
     * Форма редактирования существующей связи
     */
    @GetMapping("/edit/{id}")
    public String editBranchForm(@PathVariable Long id, Model model) {
        branchesService.findDtoById(id).ifPresent(branch -> {
            model.addAttribute("branch", branch);
            model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("isNew", false);
        });
        return "admin/branches/form";
    }

    /**
     * Сохранение связи
     */
    @PostMapping("/save")
    public String saveBranch(
            @ModelAttribute @Valid BranchDto branchDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Проверяем, существует ли уже такая связь
        if (branchDto.getId() == null &&
                branchesService.existsByClinicIdAndDepartmentId(branchDto.getClinicId(), branchDto.getDepartmentId())) {
            bindingResult.rejectValue("departmentId", "error.branch", "Эта клиника уже имеет данное отделение");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("isNew", branchDto.getId() == null);
            return "admin/branches/form";
        }

        try {
            branchesService.saveFromDto(branchDto);
            redirectAttributes.addFlashAttribute("success", "Связь успешно сохранена");
            return "redirect:/admin/branches";
        } catch (Exception e) {
            model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("isNew", branchDto.getId() == null);
            model.addAttribute("error", "Ошибка при сохранении связи: " + e.getMessage());
            return "admin/branches/form";
        }
    }

    /**
     * Удаление связи
     */
    @GetMapping("/delete/{id}")
    public String deleteBranch(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            branchesService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Связь успешно удалена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении связи: " + e.getMessage());
        }
        return "redirect:/admin/branches";
    }

    /**
     * Быстрое создание связи между клиникой и отделением
     */
    @PostMapping("/quick-create")
    public String quickCreateBranch(
            @RequestParam Long clinicId,
            @RequestParam Long departmentId,
            RedirectAttributes redirectAttributes) {

        try {
            // Проверяем, существует ли уже такая связь
            if (branchesService.existsByClinicIdAndDepartmentId(clinicId, departmentId)) {
                redirectAttributes.addFlashAttribute("warning", "Эта клиника уже имеет данное отделение");
                return "redirect:/admin/clinics/edit/" + clinicId;
            }

            branchesService.createBranch(clinicId, departmentId);
            redirectAttributes.addFlashAttribute("success", "Отделение успешно добавлено в клинику");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при добавлении отделения: " + e.getMessage());
        }

        // Перенаправляем на страницу редактирования клиники
        return "redirect:/admin/clinics/edit/" + clinicId;
    }

    /**
     * Быстрое удаление связи между клиникой и отделением
     */
    @GetMapping("/quick-delete")
    public String quickDeleteBranch(
            @RequestParam Long clinicId,
            @RequestParam Long departmentId,
            RedirectAttributes redirectAttributes) {

        try {
            branchesService.deleteByClinicIdAndDepartmentId(clinicId, departmentId);
            redirectAttributes.addFlashAttribute("success", "Отделение успешно удалено из клиники");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении отделения: " + e.getMessage());
        }

        // Перенаправляем на страницу редактирования клиники
        return "redirect:/admin/clinics/edit/" + clinicId;
    }
}