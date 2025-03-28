package com.example.cliniktour.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin")
@Controller
@RequiredArgsConstructor
public class Dashboard {
    private static final String ADMIN_AUTH_KEY = "adminAuthenticated";



    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Проверяем, авторизован ли пользователь
        if (!Boolean.TRUE.equals(session.getAttribute(ADMIN_AUTH_KEY))) {
            return "redirect:/admin/login";
        }

        // Здесь можно добавить различные данные для отображения на дашборде
        // model.addAttribute("clinicsCount", clinicService.getClinicCount());

        return "admin/dashboard";
    }
}
