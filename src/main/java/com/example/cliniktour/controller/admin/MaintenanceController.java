package com.example.cliniktour.controller.admin;

import com.example.cliniktour.service.ClinicService;
import com.example.cliniktour.service.ServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {


    private final ClinicService clinicService;
    private final ServiceService serviceService;

    public MaintenanceController(ClinicService clinicService , ServiceService serviceService) {
        this.clinicService = clinicService;
        this.serviceService= serviceService;
    }

    @GetMapping("/clean-descriptions")
    public ResponseEntity<String> cleanDescriptions() {
        clinicService.cleanAllClinicDescriptions();
        return ResponseEntity.ok("Все описания клиник очищены от лишнего форматирования");
    }

    @GetMapping("/clean-services-descriptions")
    public ResponseEntity<String> cleanServicesDescriptions() {
        serviceService.cleanAllServicesDescriptions();
        return ResponseEntity.ok("Все описания сервисов очищены от лишнего форматирования");
    }

}