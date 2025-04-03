package com.example.cliniktour.controller;

import com.example.cliniktour.dto.ServiceCreateAppointmentDTO;
import com.example.cliniktour.model.Appointment;
import com.example.cliniktour.model.Service;
import com.example.cliniktour.repository.ServiceRepository;
import com.example.cliniktour.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
@RequiredArgsConstructor
@Controller
@RequestMapping("/appointments")
public class ClientAppointmentController {

    private  final  ServiceRepository serviceRepository;
    private  final AppointmentService appointmentService;



    /**
     * Обработка формы записи на услугу
     */
    @PostMapping("/create")
    public String createAppointment(
            @Valid @ModelAttribute("appointmentDTO") ServiceCreateAppointmentDTO appointmentDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Проверка наличия ошибок валидации
        if (bindingResult.hasErrors()) {
            // Получаем услугу по ID для отображения информации в форме
            Optional<Service> serviceOpt = serviceRepository.findById(appointmentDTO.getServiceId());
            if (serviceOpt.isPresent()) {
                model.addAttribute("service", serviceOpt.get());
            }
            return "services/view";
        }

        // Создаем запись через сервисный слой
        Optional<Appointment> appointmentOpt = appointmentService.createServiceAppointment(appointmentDTO);

        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            Service service = appointment.getService();

            // Добавление флеш-сообщения об успехе
            redirectAttributes.addFlashAttribute("successMessage",
                    "Ваша заявка на услугу \"" + service.getTitle() + "\" успешно отправлена! " +
                            "Мы свяжемся с вами в ближайшее время.");

            return "redirect:/appointments/thank-you";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Произошла ошибка при обработке заявки. Услуга не найдена.");
            return "redirect:/services";
        }
    }

    /**
     * Страница благодарности после успешной записи
     */
    @GetMapping("/thank-you")
    public String thankYouPage() {
        return "services/thank-you";
    }
}