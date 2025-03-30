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

//
//    /**
//     * Обработка форм обратной связи с различных страниц
//     */
//    @PostMapping("/consultations/submit")
//    public String requestConsultation(
//            @RequestParam String fullName,
//            @RequestParam String email,
//            @RequestParam String phone,
//            @RequestParam String message,
//            @RequestParam(required = false) Long clinicId,
//            @RequestParam(required = false) Long departmentId,
//            @RequestParam(required = false) Long doctorId,
//            RedirectAttributes redirectAttributes) {
//
//        try {
//            // Разделяем полное имя на имя и фамилию (если возможно)
//            String firstName = fullName;
//            String lastName = "";
//            if (fullName.contains(" ")) {
//                String[] nameParts = fullName.split(" ", 2);
//                firstName = nameParts[0];
//                lastName = nameParts[1];
//            }
//
//            // Добавляем информацию о клинике/отделении/враче в сообщение
//            StringBuilder messageBuilder = new StringBuilder(message);
//            if (clinicId != null) {
//                messageBuilder.append("\n\nЗапрос связан с клиникой ID: ").append(clinicId);
//            }
//            if (departmentId != null) {
//                messageBuilder.append("\n\nЗапрос связан с отделением ID: ").append(departmentId);
//            }
//            if (doctorId != null) {
//                messageBuilder.append("\n\nЗапрос связан с врачом ID: ").append(doctorId);
//            }
//
//            contactRequestService.createContactRequest(firstName, lastName, email, phone, messageBuilder.toString());
//            redirectAttributes.addFlashAttribute("success", "Ваша заявка на консультацию успешно отправлена! Наш специалист свяжется с вами в ближайшее время.");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Произошла ошибка при отправке заявки. Пожалуйста, попробуйте еще раз.");
//        }
//
//        // Определяем страницу для возврата
//        String returnUrl = "/contact";
//        if (clinicId != null) {
//            returnUrl = "/clinics/" + clinicId;
//        } else if (departmentId != null) {
//            returnUrl = "/departments/" + departmentId;
//        } else if (doctorId != null) {
//            returnUrl = "/doctors/" + doctorId;
//        }
//
//        return "redirect:" + returnUrl;
//    }
}