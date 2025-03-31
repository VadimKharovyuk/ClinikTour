package com.example.cliniktour.controller.admin;

import com.example.cliniktour.dto.testimonial.TestimonialDetailDTO;
import com.example.cliniktour.dto.testimonial.TestimonialListDTO;
import com.example.cliniktour.service.TestimonialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Slf4j
@Controller
@RequestMapping("/admin/testimonials")
@RequiredArgsConstructor
public class AdminTestimonialController {

    private final TestimonialService testimonialService;

    /**
     * Отображение списка всех отзывов в админке
     */
    @GetMapping
    public String getAllTestimonials(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        Page<TestimonialListDTO> testimonials = testimonialService.getAllTestimonials(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        model.addAttribute("testimonials", testimonials.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", testimonials.getTotalPages());
        model.addAttribute("totalItems", testimonials.getTotalElements());

        return "admin/testimonials/list";
    }

    /**
     * Просмотр деталей отзыва в админке
     */
    @GetMapping("/{id}")
    public String getTestimonialDetail(@PathVariable Long id, Model model) {
        try {
            TestimonialDetailDTO testimonial = testimonialService.getTestimonialById(id);
            model.addAttribute("testimonial", testimonial);
            return "admin/testimonials/detail";
        } catch (Exception e) {
            log.error("Ошибка при получении отзыва с ID {}: {}", id, e.getMessage());
            model.addAttribute("errorMessage", "Отзыв не найден");
            return "admin/error";
        }
    }

    /**
     * Удаление отзыва
     */
    @PostMapping("/{id}/delete")
    public String deleteTestimonial(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            testimonialService.deleteTestimonial(id);
            log.info("Отзыв с ID {} был успешно удален из админки", id);
            redirectAttributes.addFlashAttribute("successMessage", "Отзыв успешно удален");
        } catch (Exception e) {
            log.error("Ошибка при удалении отзыва с ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении отзыва");
        }

        return "redirect:/admin/testimonials";
    }

    /**
     * Удаление только фотографии отзыва
     */
    @PostMapping("/{id}/delete-photo")
    public String deleteTestimonialPhoto(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            boolean success = testimonialService.deleteTestimonialPhoto(id);
            if (success) {
                log.info("Фото отзыва с ID {} было успешно удалено из админки", id);
                redirectAttributes.addFlashAttribute("successMessage", "Фотография отзыва успешно удалена");
            } else {
                redirectAttributes.addFlashAttribute("warningMessage", "Не удалось удалить фотографию или она отсутствует");
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении фото отзыва с ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении фотографии отзыва");
        }

        return "redirect:/admin/testimonials/" + id;
    }
}