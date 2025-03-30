package com.example.cliniktour.controller.admin;

import com.example.cliniktour.dto.ContactRequestDto;
import com.example.cliniktour.enums.ContactRequestStatus;
import com.example.cliniktour.service.ContactRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/contact-requests")
@RequiredArgsConstructor
public class ContactRequestAdminController {

    private final ContactRequestService contactRequestService;

    /**
     * Отображение списка контактных запросов с пагинацией
     */
    @GetMapping
    public String listContactRequests(
            @RequestParam(required = false) ContactRequestStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            Model model) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<ContactRequestDto> contactRequestPage;

        if (search != null && !search.trim().isEmpty()) {
            // Поиск по имени или email
            model.addAttribute("contactRequests", contactRequestService.searchContactRequests(search));
            model.addAttribute("search", search);
        } else if (status != null) {
            // Фильтр по статусу
            contactRequestPage = contactRequestService.getContactRequestsByStatus(status, pageable);
            model.addAttribute("contactRequestPage", contactRequestPage);
            model.addAttribute("status", status);
        } else {
            // Все запросы с пагинацией
            contactRequestPage = contactRequestService.getContactRequestPage(pageable);
            model.addAttribute("contactRequestPage", contactRequestPage);
        }

        // Общие параметры
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sort);
        model.addAttribute("sortDirection", direction);
        model.addAttribute("statuses", ContactRequestStatus.values());
        model.addAttribute("newRequestsCount", contactRequestService.countNewContactRequests());

        return "admin/contact-requests/list";
    }

    /**
     * Просмотр детальной информации о контактном запросе
     */
    @GetMapping("/view/{id}")
    public String viewContactRequest(@PathVariable Long id, Model model) {
        Optional<ContactRequestDto> contactRequestOpt = contactRequestService.getContactRequestDtoById(id);

        if (contactRequestOpt.isPresent()) {
            model.addAttribute("contactRequest", contactRequestOpt.get());
            model.addAttribute("statuses", ContactRequestStatus.values());
            return "admin/contact-requests/view";
        }

        return "redirect:/admin/contact-requests";
    }

    /**
     * Обновление статуса контактного запроса
     */
    @PostMapping("/update-status/{id}")
    public String updateContactRequestStatus(
            @PathVariable Long id,
            @RequestParam ContactRequestStatus status,
            RedirectAttributes redirectAttributes) {

        try {
            contactRequestService.updateContactRequestStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Статус запроса успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении статуса: " + e.getMessage());
        }

        return "redirect:/admin/contact-requests/view/" + id;
    }

    /**
     * Удаление контактного запроса
     */
    @GetMapping("/delete/{id}")
    public String deleteContactRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            contactRequestService.deleteContactRequest(id);
            redirectAttributes.addFlashAttribute("success", "Контактный запрос успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении запроса: " + e.getMessage());
        }

        return "redirect:/admin/contact-requests";
    }
}