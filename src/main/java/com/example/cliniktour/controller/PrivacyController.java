package com.example.cliniktour.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер для страниц политики конфиденциальности
 */
@Controller
public class PrivacyController {

    /**
     * Страница политики конфиденциальности
     */
    @GetMapping("/privacy")
    public String privacyPolicy(Model model) {
        model.addAttribute("pageTitle", "Политика конфиденциальности");
        return "privacy-policy";
    }
    /**
     * Страница пользовательского соглашения
     */
    @GetMapping("/terms")
    public String terms(Model model) {
        model.addAttribute("pageTitle", "Пользовательское соглашение");
        return "terms";
    }
}