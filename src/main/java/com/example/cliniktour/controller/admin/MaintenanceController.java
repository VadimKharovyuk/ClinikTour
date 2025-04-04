package com.example.cliniktour.controller.admin;

import com.example.cliniktour.service.ClinicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

    private final ClinicService clinicService;

    public MaintenanceController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @GetMapping("/clean-descriptions")
    public ResponseEntity<String> cleanDescriptions() {
        clinicService.cleanAllClinicDescriptions();
        return ResponseEntity.ok("Все описания клиник очищены от лишнего форматирования");
    }
}