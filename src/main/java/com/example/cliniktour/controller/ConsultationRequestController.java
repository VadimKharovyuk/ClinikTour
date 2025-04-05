package com.example.cliniktour.controller;

import com.example.cliniktour.dto.ConsultationRequestDto;
import com.example.cliniktour.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/consultations")
@RequiredArgsConstructor
public class ConsultationRequestController {

    private final AppointmentService appointmentService;

    @PostMapping("/submit")
    public String submitConsultation(@ModelAttribute ConsultationRequestDto consultationRequestDto,
                                     RedirectAttributes redirectAttributes) {
        try {
            appointmentService.createConsultationRequest(consultationRequestDto);
            redirectAttributes.addFlashAttribute("success", "Заявка на консультацию успешно отправлена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Произошла ошибка при отправке заявки: " + e.getMessage());
        }
//        return "redirect:/departments/" + consultationRequestDto.getDepartmentId();
        return "redirect:/appointments/thank-you";
    }
}