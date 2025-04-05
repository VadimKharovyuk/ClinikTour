package com.example.cliniktour.controller.admin;

import com.example.cliniktour.model.IPVisit;
import com.example.cliniktour.service.IPVisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RequiredArgsConstructor
@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    private final IPVisitService ipVisitService;

    @GetMapping
    public String showStatistics(Model model) {
        // Получаем список последних 50 посещений для отображения в таблице
        model.addAttribute("visits", ipVisitService.getRecentVisits(50));

        // Получаем статистику для отображения на дашборде
        model.addAttribute("stats", ipVisitService.getVisitStats());

        return "statistics";
    }

    @GetMapping("/data")
    @ResponseBody
    public Map<String, Object> getStatisticsData() {
        Map<String, Object> data = new HashMap<>();

        // Получаем статистику из сервиса
        data.put("visitStats", ipVisitService.getVisitStats());

        // Последние 20 посещений для таблицы
        List<IPVisit> recentVisits = ipVisitService.getRecentVisits(20);
        data.put("recentVisits", recentVisits);

        return data;
    }

    // Опциональный эндпоинт для экспорта статистики в CSV
    @GetMapping("/export-csv")
    @ResponseBody
    public String exportToCsv() {
        StringBuilder csv = new StringBuilder();
        csv.append("IP Address,Location,Visit Count,First Visit,Last Visit\n");

        for (IPVisit visit : ipVisitService.getAllVisits()) {
            csv.append(visit.getIpAddress()).append(",")
                    .append(visit.getLocation() != null ? visit.getLocation().replace(",", ";") : "Unknown").append(",")
                    .append(visit.getVisitCount()).append(",")
                    .append(visit.getFirstVisit()).append(",")
                    .append(visit.getLastVisit()).append("\n");
        }

        return csv.toString();
    }
}