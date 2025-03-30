
package com.example.cliniktour.controller;

import com.example.cliniktour.dto.ClinicConsultationDTO;
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
public class ClinicConsultationController {

    private final AppointmentService appointmentService;

    @PostMapping("/request")
    public String requestConsultation(@ModelAttribute ClinicConsultationDTO consultationDTO,
                                      RedirectAttributes redirectAttributes) {
        try {
            appointmentService.createClinicConsultation(consultationDTO);
            redirectAttributes.addFlashAttribute("success", "Заявка на консультацию успешно отправлена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Произошла ошибка при отправке заявки: " + e.getMessage());
        }

        return "redirect:/clinics/" + consultationDTO.getClinicId();
    }
}