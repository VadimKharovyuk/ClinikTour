package com.example.cliniktour.controller;

import com.example.cliniktour.dto.ClinicDto;
import com.example.cliniktour.dto.testimonial.TestimonialCreateDTO;
import com.example.cliniktour.dto.testimonial.TestimonialDetailDTO;
import com.example.cliniktour.dto.testimonial.TestimonialListDTO;
import com.example.cliniktour.service.ClinicService;
import com.example.cliniktour.service.TestimonialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/testimonials")
@RequiredArgsConstructor
public class TestimonialController {

    private final TestimonialService testimonialService;
    private final ClinicService clinicService;

    /**
     * Отображение списка отзывов с пагинацией
     */
    @GetMapping
    public String getAllTestimonials(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<TestimonialListDTO> testimonials = testimonialService.getAllTestimonials(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        model.addAttribute("testimonials", testimonials.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", testimonials.getTotalPages());
        model.addAttribute("totalItems", testimonials.getTotalElements());

        return "testimonials/list";
    }


    /**
     * Отображение формы для создания нового отзыва
     */

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("testimonialForm", new TestimonialCreateDTO());

        // Добавляем список клиник для выбора в форме
        List<ClinicDto> clinics = clinicService.getAllClinicsList();
        model.addAttribute("clinics", clinics);

        return "testimonials/create";
    }

    /**
     * Обработка создания нового отзыва
     */
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createTestimonial(
            @Valid @ModelAttribute("testimonialForm") TestimonialCreateDTO testimonialForm,
            BindingResult bindingResult,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            RedirectAttributes redirectAttributes) {


        if (bindingResult.hasErrors()) {
            log.warn("Форма отзыва содержит ошибки валидации");
            return "testimonials/create";
        }

        try {
            TestimonialDetailDTO createdTestimonial = testimonialService.createTestimonial(testimonialForm, photo);
            log.info("Отзыв успешно создан с ID: {}", createdTestimonial.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Спасибо за ваш отзыв!");
            return "redirect:/testimonials";
        } catch (Exception e) {
            log.error("Ошибка при создании отзыва: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла ошибка при сохранении отзыва. Пожалуйста, попробуйте снова.");
            return "redirect:/testimonials/create";
        }
    }



    /**
     * Отображение деталей конкретного отзыва
     */
    @GetMapping("/{id}")
    public String getTestimonialDetail(@PathVariable Long id, Model model) {
        try {
            TestimonialDetailDTO testimonial = testimonialService.getTestimonialById(id);
            model.addAttribute("testimonial", testimonial);
            return "testimonials/detail";
        } catch (Exception e) {
            log.error("Ошибка при получении отзыва с ID {}: {}", id, e.getMessage());
            model.addAttribute("errorMessage", "Отзыв не найден");
            return "error/404";
        }
    }



    /**
     * Возвращает HTML-страницу с отзывами по клинике
     */
    @GetMapping("/clinic-fragment/{clinicId}")
    public String getTestimonialsByClinicFragment(
            @PathVariable Long clinicId,
            Model model) {
        List<TestimonialListDTO> clinicTestimonials = testimonialService.getTestimonialsByClinicId(clinicId);
        model.addAttribute("testimonials", clinicTestimonials);
        model.addAttribute("clinicId", clinicId);

        // Возвращаем шаблон
        return "testimonials/testimonial-clinic-list";
    }
}