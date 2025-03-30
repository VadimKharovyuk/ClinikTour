package com.example.cliniktour.controller;
import com.example.cliniktour.model.Appointment;
import com.example.cliniktour.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/appointments")
@RequiredArgsConstructor
public class AdminAppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public String getAllAppointments(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long clinicId,
            Model model
    ) {
        List<Appointment> appointments;

        // Получение записей с учетом фильтров
        if (doctorId != null) {
            appointments = appointmentService.getDoctorAppointments(doctorId);
        } else if (clinicId != null) {
            appointments = appointmentService.getClinicConsultations(clinicId);
        } else if (type != null && dateFrom != null && dateTo != null) {
            if ("doctor".equals(type)) {
                appointments = appointmentService.getDoctorAppointmentsByDateRange(dateFrom, dateTo);
            } else if ("clinic".equals(type)) {
                appointments = appointmentService.getClinicConsultationsByDateRange(dateFrom, dateTo);
            } else {
                appointments = appointmentService.getAppointmentsByDateRange(dateFrom, dateTo);
            }
        } else if (type != null) {
            if ("doctor".equals(type)) {
                appointments = appointmentService.getAllDoctorAppointments();
            } else if ("clinic".equals(type)) {
                appointments = appointmentService.getAllClinicConsultations();
            } else {
                appointments = appointmentService.getAllAppointments();
            }
        } else if (dateFrom != null && dateTo != null) {
            appointments = appointmentService.getAppointmentsByDateRange(dateFrom, dateTo);
        } else {
            appointments = appointmentService.getAllAppointments();
        }

        // Статистика для карточек
        int doctorAppointmentsCount = appointmentService.countDoctorAppointments();
        int clinicConsultationsCount = appointmentService.countClinicConsultations();
        int todayAppointmentsCount = appointmentService.countTodayAppointments();

        model.addAttribute("appointments", appointments);
        model.addAttribute("doctorAppointments", doctorAppointmentsCount);
        model.addAttribute("clinicConsultations", clinicConsultationsCount);
        model.addAttribute("todayAppointments", todayAppointmentsCount);

        return "admin/appointments/list";
    }

    @GetMapping("/{id}")
    public String getAppointmentDetails(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(id)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));

        // Дополнительная информация для правой колонки
        int doctorAppointmentsCount = 0;
        int clinicAppointmentsCount = 0;

        if (appointment.getDoctor() != null) {
            doctorAppointmentsCount = appointmentService.countDoctorAppointments(appointment.getDoctor().getId());
        }

        if (appointment.getClinic() != null) {
            clinicAppointmentsCount = appointmentService.countClinicConsultations(appointment.getClinic().getId());
        }

        model.addAttribute("appt", appointment);
        model.addAttribute("doctorAppointments", doctorAppointmentsCount);
        model.addAttribute("clinicAppointments", clinicAppointmentsCount);

        return "admin/appointments/details";
    }

    @GetMapping("/{id}/delete")
    public String deleteAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.deleteAppointment(id);
            redirectAttributes.addFlashAttribute("success", "Запись успешно удалена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении записи: " + e.getMessage());
        }
        return "redirect:/admin/appointments";
    }
}