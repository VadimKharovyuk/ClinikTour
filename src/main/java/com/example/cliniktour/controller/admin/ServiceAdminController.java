package com.example.cliniktour.controller.admin;

import com.example.cliniktour.dto.ServiceDto;
import com.example.cliniktour.service.ServiceService;
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
import java.util.Optional;

@Controller
@RequestMapping("/admin/services")
@RequiredArgsConstructor
public class ServiceAdminController {

    private final ServiceService serviceService;

    /**
     * Отображение списка всех услуг с пагинацией
     */
    @GetMapping
    public String listServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<ServiceDto> servicePage = serviceService.getServicePage(pageable);

        model.addAttribute("servicePage", servicePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sort);
        model.addAttribute("totalPages", servicePage.getTotalPages());
        model.addAttribute("totalItems", servicePage.getTotalElements());

        // Добавляем возможные поля для сортировки
        model.addAttribute("sortFields", new String[]{"title", "price"});

        return "admin/services/list";
    }

    /**
     * Форма создания новой услуги
     */
    @GetMapping("/create")
    public String createServiceForm(Model model) {
        model.addAttribute("service", new ServiceDto());
        model.addAttribute("isNew", true);

        return "admin/services/form";
    }

    /**
     * Форма редактирования существующей услуги
     */
    @GetMapping("/edit/{id}")
    public String editServiceForm(@PathVariable Long id, Model model) {
        Optional<ServiceDto> serviceDto = serviceService.getServiceDtoById(id);

        if (serviceDto.isPresent()) {
            model.addAttribute("service", serviceDto.get());
            model.addAttribute("isNew", false);
            return "admin/services/form";
        }

        return "redirect:/admin/services";
    }

    /**
     * Просмотр информации об услуге
     */
    @GetMapping("/view/{id}")
    public String viewService(@PathVariable Long id, Model model) {
        Optional<ServiceDto> serviceDto = serviceService.getServiceDtoById(id);

        if (serviceDto.isPresent()) {
            model.addAttribute("service", serviceDto.get());
            return "admin/services/view";
        }

        return "redirect:/admin/services";
    }

    /**
     * Сохранение услуги (новой или существующей)
     */
    @PostMapping("/save")
    public String saveService(
            @ModelAttribute @Valid ServiceDto serviceDto,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", serviceDto.getId() == null);
            return "admin/services/form";
        }

        try {
            // Сохраняем услугу с изображением
            serviceService.saveServiceFromDtoWithImage(serviceDto, image);

            // Добавляем сообщение об успехе
            redirectAttributes.addFlashAttribute("success", "Услуга успешно сохранена");

            return "redirect:/admin/services";
        } catch (IOException e) {
            // Ошибка при загрузке изображения
            model.addAttribute("isNew", serviceDto.getId() == null);
            model.addAttribute("error", "Ошибка при загрузке изображения: " + e.getMessage());
            return "admin/services/form";
        } catch (Exception e) {
            // Другие ошибки
            model.addAttribute("isNew", serviceDto.getId() == null);
            model.addAttribute("error", "Ошибка при сохранении услуги: " + e.getMessage());
            return "admin/services/form";
        }
    }

    /**
     * Удаление услуги
     */
    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            serviceService.deleteService(id);
            redirectAttributes.addFlashAttribute("success", "Услуга успешно удалена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении услуги: " + e.getMessage());
        }

        return "redirect:/admin/services";
    }

    /**
     * Поиск услуг
     */
    @GetMapping("/search")
    public String searchServices(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        model.addAttribute("services", serviceService.searchServices(query));
        model.addAttribute("query", query);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        return "admin/services/search-results";
    }
}