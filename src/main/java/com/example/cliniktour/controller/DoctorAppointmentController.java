
package com.example.cliniktour.controller;

import com.example.cliniktour.dto.DoctorAppointmentDTO;
import com.example.cliniktour.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class DoctorAppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/request")
    public String requestAppointment(@ModelAttribute DoctorAppointmentDTO appointmentDTO,
                                     RedirectAttributes redirectAttributes) {
        try {
            appointmentService.createDoctorAppointment(appointmentDTO);
            redirectAttributes.addFlashAttribute("success", "Заявка на приём успешно отправлена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Произошла ошибка при отправке заявки: " + e.getMessage());
        }

        return "redirect:/doctors/" + appointmentDTO.getDoctorId();
    }
}