package com.example.cliniktour.controller;

import com.example.cliniktour.model.Service;
import com.example.cliniktour.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AboutController {

    private final ServiceService serviceService;

    @Autowired
    public AboutController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping("/about")
    public String aboutPage(Model model) {
        // Получаем популярные услуги для отображения в разделе "Наши услуги"
        List<Service> popularServices = serviceService.getPopularServices(4);
        model.addAttribute("popularServices", popularServices);

        // Статистические данные о компании
        model.addAttribute("clientsCount", 5000);
        model.addAttribute("partnersCount", 25);
        model.addAttribute("yearsOfExperience", 7);
        model.addAttribute("satisfactionRate", 98);

        return "about";
    }
}