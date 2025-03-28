package com.example.cliniktour.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {


    @Value("${admin.username:admin}")
    private String ADMIN_USERNAME;

    @Value("${admin.password:secure_password}")
    private String ADMIN_PASSWORD;

    private static final String ADMIN_AUTH_KEY = "adminAuthenticated";

    /**
     * Страница входа в админ-панель
     */
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // Если администратор уже авторизован, перенаправляем на дашборд
        if (Boolean.TRUE.equals(session.getAttribute(ADMIN_AUTH_KEY))) {
            return "redirect:/admin/dashboard";
        }
        return "admin/login";
    }


    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        // Проверка учетных данных
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            // Успешная аутентификация - устанавливаем флаг в сессии
            session.setAttribute(ADMIN_AUTH_KEY, true);
            return "redirect:/admin/dashboard";
        } else {
            // Неудачная аутентификация
            redirectAttributes.addFlashAttribute("error", "Неверное имя пользователя или пароль");
            return "redirect:/admin/login";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Удаляем флаг аутентификации из сессии
        session.removeAttribute(ADMIN_AUTH_KEY);
        return "redirect:/admin/login";
    }

}