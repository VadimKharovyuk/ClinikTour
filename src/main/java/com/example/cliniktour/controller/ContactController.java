package com.example.cliniktour.controller;

import com.example.cliniktour.service.ContactRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ContactController {

    private final ContactRequestService contactRequestService;

    /**
     * Отображение страницы контактов
     */
    @GetMapping("/contact")
    public String contactPage(Model model) {
        return "contact";
    }

    /**
     * Обработка отправки контактной формы
     */
    @PostMapping("/contact/submit")
    public String submitContactForm(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String message,
            RedirectAttributes redirectAttributes) {

        try {
            contactRequestService.createContactRequest(firstName, lastName, email, phone, message);
            redirectAttributes.addFlashAttribute("success", "Ваше сообщение успешно отправлено! Мы свяжемся с вами в ближайшее время.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Произошла ошибка при отправке сообщения. Пожалуйста, попробуйте еще раз.");
            // Возвращаем введенные данные для повторного заполнения формы
            redirectAttributes.addFlashAttribute("firstName", firstName);
            redirectAttributes.addFlashAttribute("lastName", lastName);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("phone", phone);
            redirectAttributes.addFlashAttribute("message", message);
        }

        return "redirect:/contact";
    }

}