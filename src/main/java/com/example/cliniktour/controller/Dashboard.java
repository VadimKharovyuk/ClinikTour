package com.example.cliniktour.controller;

import com.example.cliniktour.dto.ClinicDto;
import com.example.cliniktour.dto.ContactRequestDto;
import com.example.cliniktour.dto.DoctorListDto;
import com.example.cliniktour.service.ClinicService;
import com.example.cliniktour.service.ContactRequestService;
import com.example.cliniktour.service.DepartmentService;
import com.example.cliniktour.service.DoctorService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/admin")
@Controller
@RequiredArgsConstructor
public class Dashboard {
    private static final String ADMIN_AUTH_KEY = "adminAuthenticated";
    private final ClinicService clinicService;
    private final ContactRequestService contactRequestService;
    private final DoctorService doctorService;
    private final DepartmentService departmentService;


    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Проверяем, авторизован ли пользователь
        if (!Boolean.TRUE.equals(session.getAttribute(ADMIN_AUTH_KEY))) {
            return "redirect:/admin/login";
        }


        model.addAttribute("clinicsCount", clinicService.getClinicCount());
        model.addAttribute("newRequestsCount", contactRequestService.countNewContactRequests());
        model.addAttribute("doctorsCount", doctorService.getDoctorsCount());
        model.addAttribute("departmentsCount", departmentService.getDepartmentsCount());


        // Получаем список последних заявок (ограничение по 5 заявок)
        List<ContactRequestDto> latestRequests = contactRequestService.getLatestContactRequests(5);
        model.addAttribute("latestRequests", latestRequests);

        // Получаем список последних клиник (ограничение по 5 клиник)
        List<ClinicDto> latestClinics = clinicService.getLatestClinics(5);
        model.addAttribute("latestClinics", latestClinics);

        // Получаем список последних врачей (ограничение по 5 врачей)
        List<DoctorListDto> latestDoctors = doctorService.getLatestDoctors(5);
        model.addAttribute("latestDoctors", latestDoctors);

        return "admin/dashboard";
    }
}
