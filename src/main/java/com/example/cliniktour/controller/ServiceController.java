package com.example.cliniktour.controller;

import com.example.cliniktour.dto.ServiceDto;
import com.example.cliniktour.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    /**
     * Отображение списка всех услуг с пагинацией
     */
    @GetMapping
    public String listServices(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "title") String sort,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        // Если указан поисковый запрос, используем поиск
        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("services", serviceService.searchServices(search));
            model.addAttribute("search", search);
        }
        // Если указан ценовой диапазон, используем фильтр по цене
        else if (minPrice != null || maxPrice != null) {
            model.addAttribute("services", serviceService.searchServicesByPriceRange(minPrice, maxPrice));
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
        }
        // В остальных случаях просто показываем страницу
        else {
            Page<ServiceDto> servicePage = serviceService.getServicePage(pageable);
            model.addAttribute("servicePage", servicePage);
        }

        // Добавляем общие параметры
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sort);

        // Добавляем рекомендуемые услуги для боковой панели
        model.addAttribute("featuredServices", serviceService.getFeaturedServices(3));

        return "services/list";
    }

    /**
     * Отображение детальной информации об услуге
     */
    @GetMapping("/{id}")
    public String viewService(@PathVariable Long id, Model model) {
        Optional<ServiceDto> serviceOpt = serviceService.getServiceDtoById(id);

        if (serviceOpt.isPresent()) {
            model.addAttribute("service", serviceOpt.get());

            // Получаем другие услуги для блока "Вас также может заинтересовать"
            model.addAttribute("relatedServices", serviceService.getLatestServices(3));

            return "services/view";
        }

        return "redirect:/services";
    }

    /**
     * Поиск услуг
     */
    @GetMapping("/search")
    public String searchServices(@RequestParam String query, Model model) {
        model.addAttribute("services", serviceService.searchServices(query));
        model.addAttribute("search", query);
        model.addAttribute("featuredServices", serviceService.getFeaturedServices(3));
        return "services/search-results";
    }

    /**
     * Фильтр услуг по ценовому диапазону
     */
    @GetMapping("/price-range")
    public String servicesByPriceRange(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Model model) {

        model.addAttribute("services", serviceService.searchServicesByPriceRange(minPrice, maxPrice));
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("featuredServices", serviceService.getFeaturedServices(3));

        return "services/price-filter";
    }

    /**
     * Отображение рекомендуемых услуг
     */
    @GetMapping("/featured")
    public String featuredServices(Model model) {
        model.addAttribute("services", serviceService.getFeaturedServices(10));
        model.addAttribute("title", "Рекомендуемые услуги");
        return "services/featured";
    }
}